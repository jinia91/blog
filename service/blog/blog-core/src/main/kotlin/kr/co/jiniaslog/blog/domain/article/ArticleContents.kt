package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import java.io.Serializable

@Embeddable
data class ArticleContents(
    val title: String,
    val contents: String,
    val thumbnailUrl: String,
) : ValueObject, Serializable {
    val canPublish
        get() = title.isNotBlank() && contents.isNotBlank() && thumbnailUrl.isNotBlank()
    init {
        validate()
    }
    override fun validate() {
        require(title.length <= 100) { "제목은 100자를 넘을 수 없습니다." }
    }

    fun validateOnPublish() {
        require(this.title.isNotBlank()) { "제목은 필수입니다." }
        require(this.contents.isNotBlank()) { "내용은 필수입니다." }
        require(this.thumbnailUrl.isNotBlank()) { "썸네일은 필수입니다." }
    }

    companion object {
        val EMPTY = ArticleContents("", "", "")
    }
}
