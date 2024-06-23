package kr.co.jiniaslog.user.domain.auth.token

@JvmInline
value class RefreshToken(override val value: String) : AuthToken {
    init {
        validate()
    }

    override fun validate() {
        require(value.isNotBlank()) { "리프레시 토큰은 빈 값이 될 수 없습니다." }
    }
}
