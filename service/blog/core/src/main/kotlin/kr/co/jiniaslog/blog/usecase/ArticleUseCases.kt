package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.WriterId

/**
 * #############
 * # Use Cases #
 * #############
 */
interface ArticleUseCases {
    /**
     * 최초의 아티클을 생성하고 최초 커밋한다.
     */
    suspend fun init(articleInitCommand: ArticleInitCommand): InitialInfo
}

/**
 * #############
 * # Commands  #
 * #############
 */
data class ArticleInitCommand(
    val writerId: WriterId,
)

data class InitialInfo(
    val articleId: ArticleId,
)
