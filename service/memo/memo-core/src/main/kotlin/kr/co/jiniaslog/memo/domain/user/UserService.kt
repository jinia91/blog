package kr.co.jiniaslog.memo.domain.user

import kr.co.jiniaslog.memo.domain.memo.AuthorId

interface UserService {
    fun retrieveAdminUserIds(): List<AuthorId>
}
