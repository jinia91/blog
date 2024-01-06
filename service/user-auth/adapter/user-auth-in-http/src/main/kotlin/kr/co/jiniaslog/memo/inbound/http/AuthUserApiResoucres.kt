package kr.co.jiniaslog.memo.inbound.http

data class RedirectUrlResponse(
    val url: String,
)

data class OAuthLoginRequest(
    val code: String,
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val nickName: String,
    val email: String,
)
