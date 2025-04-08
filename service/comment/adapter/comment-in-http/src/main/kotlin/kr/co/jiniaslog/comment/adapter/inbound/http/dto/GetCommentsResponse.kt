package kr.co.jiniaslog.comment.adapter.inbound.http.dto

import kr.co.jiniaslog.comment.domain.CommentVo

data class GetCommentsResponse(
    val comments: List<CommentVo>
)
