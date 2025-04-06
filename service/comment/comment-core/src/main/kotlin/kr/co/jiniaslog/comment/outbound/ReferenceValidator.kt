package kr.co.jiniaslog.comment.outbound

import kr.co.jiniaslog.comment.domain.Comment
import kr.co.jiniaslog.comment.domain.ReferenceId
import org.springframework.stereotype.Component

@Component
class ReferenceValidator(
    private val articleService: ArticleService,
) {
    fun isValid(refType: Comment.RefType, refId: ReferenceId): Boolean {
        return when (refType) {
            Comment.RefType.ARTICLE -> isValidArticle(refId)
        }
    }

    private fun isValidArticle(refId: ReferenceId): Boolean {
        return articleService.isExist(refId.value)
    }
}
