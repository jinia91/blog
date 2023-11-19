package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleCommitVersion
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.writer.WriterId

interface ArticleViewUseCases :
    ArticleViewUpsertUseCase

interface ArticleViewUpsertUseCase {
    suspend fun upsert(command: ArticleViewUpsertCommand): ArticleViewUpsertInfo

    data class ArticleViewUpsertCommand(
        val articleId: ArticleId,
        val writerId: WriterId,
        val categoryId: CategoryId?,
        val headVersion: ArticleCommitVersion,
    )

    data class ArticleViewUpsertInfo(
        val articleId: ArticleId,
        val headVersion: ArticleCommitVersion,
    )
}
