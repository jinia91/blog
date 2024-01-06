package kr.co.jiniaslog.user.domain.auth

interface ProviderAdapter {
    val provider: Provider
    val tokenUrl: Url
    val userInfoUrl: Url
    val clientId: String
    val clientSecret: String
    val redirectUrl: Url

    fun getUserInfo(code: AuthorizationCode): ProviderUserInfo

    fun getLoginUrl(): Url
}
