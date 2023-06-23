package kr.co.jiniaslog.blogcore.domain.user

interface UserServiceClient {
    fun doesUserExist(userId: UserId): Boolean
}
