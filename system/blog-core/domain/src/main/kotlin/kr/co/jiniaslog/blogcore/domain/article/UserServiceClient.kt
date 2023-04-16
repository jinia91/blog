package kr.co.jiniaslog.blogcore.domain.article

interface UserServiceClient {
    fun userExists(userId: UserId): Boolean
}
