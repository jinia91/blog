package kr.co.jiniaslog.comment.domain

import kr.co.jiniaslog.comment.outbound.ArticleService
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
