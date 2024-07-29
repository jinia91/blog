package kr.co.jiniaslog.admin.adapter.inbound.http.dto

/**
 * 목유저 생성 요청
 *
 * @property role 유저 역할, ex) "ADMIN, "USER
 * @property id 바인딩할 유저 아이디
 */
data class AdminCreateUserRequest(
    val role: String,
    val id: Long,
)

data class AdminCreateUserResponse(
    val id: Long,
)
