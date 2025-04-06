package kr.co.jiniaslog.comment.outbound

import kr.co.jiniaslog.comment.domain.UserInfo

interface UserService {
    fun getUserInfo(userId: Long): UserInfo
}
