package kr.co.jiniaslog.comment.outbound

import kr.co.jiniaslog.comment.domain.AuthorInfo

interface UserService {
    fun getUserInfo(userId: Long): AuthorInfo
    fun isAdmin(userId: Long): Boolean
}
