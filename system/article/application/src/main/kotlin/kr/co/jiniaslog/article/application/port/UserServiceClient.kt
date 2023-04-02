package kr.co.jiniaslog.article.application.port

import kr.co.jiniaslog.article.domain.UserId

interface UserServiceClient {
    fun findUserById(id: Long): UserId?
}
