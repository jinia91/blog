package kr.cojiniaslog.shared.adapter.inbound.http

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = [Content(schema = Schema())]
        ),
        ApiResponse(
            responseCode = "403",
            description = "인가 되지 않음",
            content = [Content(schema = Schema())]
        ),
    ]
)
annotation class CommonApiResponses
