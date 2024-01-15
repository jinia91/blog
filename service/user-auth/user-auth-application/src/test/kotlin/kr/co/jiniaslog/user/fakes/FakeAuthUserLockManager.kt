package kr.co.jiniaslog.user.fakes

import kr.co.jiniaslog.shared.core.annotation.CustomComponent
import kr.co.jiniaslog.user.application.infra.AuthUserLockManager
import kr.co.jiniaslog.user.application.usecase.IRefreshToken
import kr.co.jiniaslog.user.domain.user.UserId

@CustomComponent
class FakeAuthUserLockManager : AuthUserLockManager {
    private val lock = mutableMapOf<UserId, Boolean>()

    override fun lock(
        userId: UserId,
        block: () -> IRefreshToken.Info,
    ): IRefreshToken.Info {
        if (lock[userId] == true) {
            throw IllegalStateException("lock")
        }

        lock[userId] = true
        try {
            return block()
        } finally {
            lock[userId] = false
        }
    }

    override fun hasLock(userId: UserId): Boolean {
        return lock[userId] == true
    }
}
