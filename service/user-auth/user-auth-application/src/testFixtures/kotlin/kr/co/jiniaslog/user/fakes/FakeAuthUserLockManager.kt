package kr.co.jiniaslog.user.fakes

import kr.co.jiniaslog.user.application.infra.AuthUserLockManager
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.domain.user.UserId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class FakeAuthUserLockManager : AuthUserLockManager {
    private val locks = ConcurrentHashMap<UserId, ReentrantLock>()

    override fun lock(
        userId: UserId,
        timeOutSeconds: Int,
        block: () -> IRefreshToken.Info,
    ): IRefreshToken.Info {
        val lock = locks.computeIfAbsent(userId) { ReentrantLock() }
        val isLocked = lock.tryLock(timeOutSeconds.toLong(), TimeUnit.SECONDS)
        if (!isLocked) {
            throw RuntimeException("Unable to acquire lock within $timeOutSeconds seconds for user $userId")
        }

        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    override fun hasLock(userId: UserId): Boolean {
        return locks[userId]?.isLocked ?: false
    }
}
