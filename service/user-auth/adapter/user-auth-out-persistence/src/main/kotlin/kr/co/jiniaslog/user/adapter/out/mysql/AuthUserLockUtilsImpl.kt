package kr.co.jiniaslog.user.adapter.out.mysql

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Query
import kr.co.jiniaslog.user.application.infra.AuthUserLockManager
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.domain.user.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.sql.SQLException

private val log = KotlinLogging.logger { }

data class UserLevelLockTimeoutException(override val message: String) : RuntimeException(message)

@Component
class UserLockManager(
    private val entityManagerFactory: EntityManagerFactory,
) : AuthUserLockManager {
    companion object {
        private const val HAS_LOCK_SQL = "SELECT IS_USED_LOCK(?)"
        private const val GET_LOCK_SQL = "SELECT GET_LOCK(?, ?)"
        private const val RELEASE_LOCK_SQL = "SELECT RELEASE_LOCK(?)"
        private const val LIST_PROCESS_SQL =
            "SELECT id, user, host, db, command, time, state, info FROM information_schema.processlist WHERE ID = ?"
    }

    private enum class LockAction { GET, RELEASE }

    private data class SqlProcess(
        val id: Number?,
        val user: String?,
        val host: String?,
        val db: String?,
        val command: String? = null,
        val time: Number? = null,
        val state: String? = null,
        val info: String? = null,
    )

    override fun hasLock(userId: UserId): Boolean {
        return getLockConnectionId(userId.value.toString()) != null
    }

    private fun getLockConnectionId(userLockName: String): Number? {
        val em = entityManagerFactory.createEntityManager()
        return try {
            val query = em.createNativeQuery(HAS_LOCK_SQL).apply { setParameter(1, userLockName) }
            query.resultList.firstOrNull() as? Number
        } catch (e: Exception) {
            throw e
        } finally {
            em.close()
        }
    }

    private fun getProcessOccupiedLock(processId: Number): SqlProcess? {
        val em = entityManagerFactory.createEntityManager()
        return try {
            val processQuery = em.createNativeQuery(LIST_PROCESS_SQL).apply { setParameter(1, processId) }
            val resultList = processQuery.resultList
            return resultList.firstOrNull()?.let { raw ->
                val data = raw as Array<*>
                SqlProcess(
                    data[0] as? Number,
                    data[1] as? String,
                    data[2] as? String,
                    data[3] as? String,
                    data[4] as? String,
                    data[5] as? Number,
                    data[6] as? String,
                    data[7] as? String,
                )
            }
        } catch (e: Exception) {
            log.error("$e")
            null
        } finally {
            em.close()
        }
    }

    override fun lock(
        userId: UserId,
        timeOutSeconds: Int,
        block: () -> IRefreshToken.Info,
    ): IRefreshToken.Info {
        return executeWithLock(userId.toString(), timeOutSeconds) {
            block()
        }
    }

    /**
     * Block 을 실행하기 전에 {userLockName} 으로 user-level lock 을 얻고 실행한다.
     * timeoutSeconds 동안 lock 을 획득하지 못한다면 UserLevelLockTimeoutException 이 발생
     *
     * @param userLockName: user-level lock 이름
     * @param timeoutSeconds: 0 = 즉시 실패, negative = 무한정, positive = 정해진 초
     *
     * @throws UserLevelLockTimeoutException
     */
    @Throws(UserLevelLockTimeoutException::class)
    private fun <T> executeWithLock(
        userLockName: String,
        timeoutSeconds: Int = 0,
        block: () -> T,
    ): T {
        if (timeoutSeconds < 0) throw IllegalArgumentException("Lock($userLockName) 의 lock timeout 은 0 이상 이어야 합니다.")

        val entityManager = entityManagerFactory.createEntityManager()
        var hasAcquired = false
        return try {
            log.info("start getLock=[{}], timeoutSeconds : [{}], connection=[{}]", userLockName, timeoutSeconds, entityManager.hashCode())
            getLock(entityManager, userLockName, timeoutSeconds)
            log.info("success getLock=[{}], timeoutSeconds : [{}], connection=[{}]", userLockName, timeoutSeconds, entityManager.hashCode())
            hasAcquired = true
            block()
        } catch (e: Exception) {
            when (e) {
                is UserLevelLockTimeoutException -> throw e
                else -> throw e
            }
        } finally {
            // 잡고 있지 않은 락을 해제하려할 시 현재는 exception 을 던지므로
            // 반드시 명시적으로 release lock 을 했을 경우에만 락 해제
            // 즉 락을 얻었다고 헀는데 해제하려고 할 때 없을 때는 정말 에러이므로 exception 을 던진다.
            // 개발자가 db 에서 직접 락을 해제해줘야함
            if (hasAcquired) {
                log.info("start releaseLock=[{}], connection=[{}]", userLockName, entityManager.hashCode())
                releaseLock(entityManager, userLockName)
                log.info("success releaseLock=[{}], connection=[{}]", userLockName, entityManager.hashCode())
            }
            entityManager.close()
        }
    }

    @Throws(SQLException::class)
    private fun getLock(
        em: EntityManager,
        userLockName: String,
        timeoutSeconds: Int,
    ) {
        val q =
            em.createNativeQuery(GET_LOCK_SQL).apply {
                setParameter(1, userLockName)
                setParameter(2, timeoutSeconds)
            }

        checkResultSet(em, q, userLockName, LockAction.GET)
    }

    @Throws(SQLException::class)
    private fun releaseLock(
        em: EntityManager,
        userLockName: String,
    ) {
        val q =
            em.createNativeQuery(RELEASE_LOCK_SQL).apply {
                setParameter(1, userLockName)
            }

        checkResultSet(em, q, userLockName, LockAction.RELEASE)
    }

    @Throws(Exception::class)
    private fun checkResultSet(
        em: EntityManager,
        query: Query,
        userLockName: String,
        action: LockAction,
    ) {
        val result = query.resultList

        return when {
            action == LockAction.GET && (result.firstOrNull() == null || result.first() == BigInteger.ZERO) -> {
                val process = getLockConnectionId(userLockName)?.let(this::getProcessOccupiedLock)
                var logStr =
                    """USER LEVEL LOCK Timeout. 
                        |type = [$action], 
                        |result : [$result], 
                        |userLockName : [$userLockName], 
                        |connection=[${em.hashCode()}]"""
                        .trimMargin()
                if (process != null) {
                    logStr = "$logStr\nLock process = $process"
                }
                log.warn(logStr)
                throw UserLevelLockTimeoutException("Lock($userLockName) 을 얻는데 실패했습니다.")
            }

            action == LockAction.RELEASE && result.firstOrNull() != BigInteger.ONE -> {
                val process = getLockConnectionId(userLockName)?.let(this::getProcessOccupiedLock)
                var logStr =
                    "USER LEVEL LOCK 쿼리 결과 값이 없습니다. type = [$action], userLockName : [$userLockName], connection=[${em.hashCode()}]"
                if (process != null) {
                    logStr = "$logStr\nLock process = $process"
                }
                log.error(logStr)
            }

            else -> {
                // success
            }
        }
    }
}
