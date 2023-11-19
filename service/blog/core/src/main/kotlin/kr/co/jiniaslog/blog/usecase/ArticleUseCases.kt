package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleCommitVersion
import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.writer.WriterId

interface ArticleUseCases :
    ArticleInitCommandUseCase,
    ArticleCommitCommandUseCase,
    ArticleStagingCommandUseCase,
    ArticleDeleteCommandUseCase,
    ArticlePublishCommandUseCase

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
        val content: ArticleContent,
        val categoryId: CategoryId?,
        val thumbnailUrl: ArticleThumbnailUrl?,
    )

    data class StagingInfo(
        val articleId: ArticleId,
    )
}

/**
 * 아티클을 삭제한다
 */
interface ArticleDeleteCommandUseCase {
    suspend fun delete(command: ArticleDeleteCommand): DeleteInfo

    data class ArticleDeleteCommand(
        val articleId: ArticleId,
    )

    data class DeleteInfo(
        val result: Boolean,
    )
}

/**
 * 아티클을 공개한다
 */
interface ArticlePublishCommandUseCase {
    suspend fun publish(command: ArticlePublishCommand): PublishInfo

    data class ArticlePublishCommand(
        val articleId: ArticleId,
        val headVersion: ArticleCommitVersion,
    )

    data class PublishInfo(
        val articleId: ArticleId,
    )
}
