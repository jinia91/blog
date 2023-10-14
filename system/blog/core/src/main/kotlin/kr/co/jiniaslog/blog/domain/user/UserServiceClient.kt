package kr.co.jiniaslog.blog.domain.user

import kr.co.jiniaslog.blog.domain.user.UserId

interface UserServiceClient {
    fun doesUserExist(userId: UserId): Boolean
}
