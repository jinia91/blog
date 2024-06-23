package kr.co.jiniaslog.user.domain.auth.token

@JvmInline
value class AccessToken(override val value: String) : AuthToken {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "액세스 토큰은 빈 값이 될 수 없습니다." }
    }
}
