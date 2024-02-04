package kr.co.jiniaslog.blog.domain.article

import jakarta.persistence.Embeddable
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

@Embeddable
data class ArticleContents(
    val title: String,
    val contents: String,
    val thumbnailUrl: String,
) : ValueObject {
    override fun validate() {
        require(title.isNotBlank()) { "제목은 공백일 수 없습니다." }
        require(title.length <= 100) { "제목은 100자를 넘을 수 없습니다." }
        require(contents.isNotBlank()) { "내용은 공백일 수 없습니다." }
        require(thumbnailUrl.isNotBlank()) { "썸네일은 공백일 수 없습니다." }
    }
}
