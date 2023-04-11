package kr.co.jiniaslog.blogcore.domain.article

interface UserServiceClient {
    fun isAdmin(id: Long): Boolean
}
