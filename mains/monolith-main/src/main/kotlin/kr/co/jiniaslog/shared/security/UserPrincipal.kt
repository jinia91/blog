package kr.co.jiniaslog.shared.security

class UserPrincipal(
    val userId: Long,
    val roles: Set<String>,
)
