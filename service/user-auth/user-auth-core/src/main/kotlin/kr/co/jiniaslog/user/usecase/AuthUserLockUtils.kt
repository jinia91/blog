package kr.co.jiniaslog.user.usecase

import kr.co.jiniaslog.user.domain.user.UserId

interface AuthUserLockUtils {
    fun lock(
        userId: UserId,
        block: () -> IRefreshToken.Info,
    ): IRefreshToken.Info

    fun hasLock(userId: UserId): Boolean
}
