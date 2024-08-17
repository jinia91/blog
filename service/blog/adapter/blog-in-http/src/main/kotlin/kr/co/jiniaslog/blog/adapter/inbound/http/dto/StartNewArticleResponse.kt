package kr.co.jiniaslog.blog.adapter.inbound.http.dto

import io.swagger.v3.oas.annotations.media.Schema

data class StartNewArticleResponse(
    @Schema(name = "articleId", description = "게시글 아이디", example = "1")
    val articleId: Long,
)
