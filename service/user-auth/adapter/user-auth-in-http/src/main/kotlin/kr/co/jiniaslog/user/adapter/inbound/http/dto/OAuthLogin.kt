package kr.co.jiniaslog.user.adapter.inbound.http.dto

data class OAuthLoginRequest(
    val code: String,
)

data class OAuthLoginResponse(
    val nickName: String,
    val email: String,
    val roles: Set<String>,
    val picUrl: String?,
    val userId: Long,
)
