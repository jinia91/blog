package kr.co.jiniaslog.article.application.port

interface UserServiceClient {
    fun isAdmin(id: Long): Boolean
}
