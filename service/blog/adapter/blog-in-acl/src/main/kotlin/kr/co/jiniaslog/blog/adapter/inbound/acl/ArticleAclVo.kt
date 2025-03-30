package kr.co.jiniaslog.blog.adapter.inbound.acl

data class ArticleAclVo(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: java.time.LocalDateTime
)
