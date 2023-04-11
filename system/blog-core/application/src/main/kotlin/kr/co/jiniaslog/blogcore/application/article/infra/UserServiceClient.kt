package kr.co.jiniaslog.blogcore.application.article.infra

interface UserServiceClient {
    fun isAdmin(id: Long): Boolean
}
