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
    override fun validate() {
        require(title.length <= 100) { "제목은 100자를 넘을 수 없습니다." }
    }

    companion object {
        val EMPTY = ArticleContents("", "", "")
    }
}
