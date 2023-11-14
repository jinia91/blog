package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleCommitVersion
import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.article.WriterId
import kr.co.jiniaslog.blog.domain.category.CategoryId

interface ArticleUseCases :
    ArticleInitCommandUseCase,
    ArticleCommitCommandUseCase,
    ArticleStagingCommandUseCase

/**
 * 최초의 아티클을 생성하고 최초 커밋한다.
 */
interface ArticleInitCommandUseCase {
    suspend fun init(command: ArticleInitCommand): InitialInfo

    data class ArticleInitCommand(
        val writerId: WriterId,
    )

    data class InitialInfo(
        val articleId: ArticleId,
    )
}

/**
 * 작업중인 내용을 임시 저장한다.
 */
interface ArticleCommitCommandUseCase {
    suspend fun commit(command: ArticleCommitCommand): CommitInfo

    data class ArticleCommitCommand(
        val articleId: ArticleId,
        val title: ArticleTitle?,
        val content: ArticleContent,
        val categoryId: CategoryId?,
        val thumbnailUrl: ArticleThumbnailUrl?,
    )

    data class CommitInfo(
        val articleId: ArticleId,
        val commitId: ArticleCommitVersion,
    )
}

/**
 * 작업중인 내용을 임시 저장한다.
 */
interface ArticleStagingCommandUseCase {
    suspend fun staging(command: ArticleStagingCommand): StagingInfo

    data class ArticleStagingCommand(
        val articleId: ArticleId,
        val title: ArticleTitle?,
        val content: ArticleContent?,
        val categoryId: CategoryId?,
        val thumbnailUrl: ArticleThumbnailUrl?,
    )

    data class StagingInfo(
        val articleId: ArticleId,
    )
}
