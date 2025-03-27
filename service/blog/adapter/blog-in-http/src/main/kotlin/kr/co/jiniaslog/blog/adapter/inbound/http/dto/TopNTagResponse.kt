package kr.co.jiniaslog.blog.adapter.inbound.http.dto

data class TopNTagResponse(
    val tags: Map<Long, String>
)
