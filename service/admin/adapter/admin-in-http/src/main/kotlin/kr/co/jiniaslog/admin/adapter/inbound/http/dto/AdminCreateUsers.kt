package kr.co.jiniaslog.admin.adapter.inbound.http.dto

data class AdminCreateUserRequest(
    val role: String,
    val id: Long,
)

data class AdminCreateUserResponse(
    val id: Long,
)
