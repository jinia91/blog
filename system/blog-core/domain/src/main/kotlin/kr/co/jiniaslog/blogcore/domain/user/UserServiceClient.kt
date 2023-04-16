package kr.co.jiniaslog.blogcore.domain.user

interface UserServiceClient {
    fun userExists(userId: UserId): Boolean
}
