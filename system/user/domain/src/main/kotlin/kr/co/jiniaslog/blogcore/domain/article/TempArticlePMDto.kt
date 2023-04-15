package kr.co.jiniaslog.blogcore.domain.article

import java.io.Serializable
import java.time.LocalDateTime

/**
 * A DTO for the {@link kr.co.jiniaslog.blogcore.adapter.persistence.article.TempArticlePM} entity
 */
data class TempArticlePMDto(
    val createdDate: LocalDateTime? = null,
    val updatedDate: LocalDateTime? = null,
    val id: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val thumbnailUrl: String? = null,
    val writerId: Long? = null,
    val categoryId: Long? = null,
) : Serializable
