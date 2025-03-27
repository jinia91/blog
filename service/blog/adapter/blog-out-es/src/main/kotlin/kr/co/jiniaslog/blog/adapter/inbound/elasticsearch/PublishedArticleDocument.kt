package kr.co.jiniaslog.blog.adapter.inbound.elasticsearch

import kr.co.jiniaslog.blog.domain.article.Article.Status.PUBLISHED
import kr.co.jiniaslog.blog.domain.article.ArticleVo
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Article document
 *
 * 엘라스틱 서치 어댑터용 역직렬화 불변 객체
 *
 */
@Document(indexName = "article_changed")
data class PublishedArticleDocument(
    @Id @Field(name = "ID")
    val id: String,
    @Field(name = "TITLE")
    val title: String,
    @Field(name = "CONTENTS")
    val content: String,
    @Field(name = "STATUS")
    val status: String,
    @Field(name = "THUMBNAIL_URL")
    val thumbnailUrl: String,
    @Field(name = "CREATED_AT")
    private val createdAtTimeStamp: Long,
    @Field(name = "UPDATED_AT")
    private val updatedAtTimeStamp: Long,
) {
    val createdAt: LocalDateTime
        get() = LocalDateTime.ofEpochSecond(createdAtTimeStamp / 1000, 0, ZoneOffset.ofHours(9))
    override fun toString(): String {
        return "ArticleDocument(id='$id', title='$title', content='$content', status='$status', thumbnailUrl='$thumbnailUrl', createdAt=$createdAt)"
    }

    fun toArticleVo(): ArticleVo {
        return ArticleVo(
            id = id.toLong(),
            title = title,
            content = content,
            thumbnailUrl = thumbnailUrl,
            status = status,
            tags = emptyMap(),
            createdAt = createdAt,
            voDataStatus = PUBLISHED
        )
    }
}
