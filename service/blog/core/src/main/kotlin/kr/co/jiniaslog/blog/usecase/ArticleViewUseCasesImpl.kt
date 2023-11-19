package kr.co.jiniaslog.blog.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleRepository
import kr.co.jiniaslog.blog.domain.articleview.ArticleView
import kr.co.jiniaslog.blog.domain.articleview.ArticleViewRepository
import kr.co.jiniaslog.blog.domain.category.Category
import kr.co.jiniaslog.blog.domain.category.CategoryRepository
import kr.co.jiniaslog.blog.domain.writer.Writer
import kr.co.jiniaslog.blog.domain.writer.WriterProvider
import kr.co.jiniaslog.blog.usecase.ArticleViewUpsertUseCase.ArticleViewUpsertCommand
import kr.co.jiniaslog.blog.usecase.ArticleViewUpsertUseCase.ArticleViewUpsertInfo
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import kr.co.jiniaslog.shared.core.domain.TransactionHandler

@UseCaseInteractor
class ArticleViewUseCasesImpl(
    private val articleRepository: ArticleRepository,
    private val writerProvider: WriterProvider,
    private val categoryRepository: CategoryRepository,
    private val articleViewRepository: ArticleViewRepository,
    private val transactionHandler: TransactionHandler,
) : ArticleViewUseCases {
    override suspend fun upsert(command: ArticleViewUpsertCommand): ArticleViewUpsertInfo =
        coroutineScope {
            with(command) {
                val (article, writer, category) =
                    fetchAllAndValidate(this@coroutineScope, command)

                val articleView =
                    articleViewRepository.findById(articleId)?.update(
                        title = article.title,
                        content = article.content,
                        thumbnailUrl = article.thumbnailUrl,
                        categoryName = category?.name,
                        writer = writer.name,
                    ) ?: ArticleView.create(
                        id = articleId,
                        title = article.title,
                        content = article.content,
                        thumbnailUrl = article.thumbnailUrl,
                        categoryName = category?.name,
                        writer = writer.name,
                    )

                transactionHandler.runInRepeatableReadTransaction {
                    articleViewRepository.save(articleView)
                }

                ArticleViewUpsertInfo(
                    articleId = articleView.id,
                    headVersion = article.head,
                )
            }
        }

    private suspend fun fetchAllAndValidate(
        coroutineScope: CoroutineScope,
        command: ArticleViewUpsertCommand,
    ): Triple<Article, Writer, Category?> {
        val articleDeferred = coroutineScope.async { articleRepository.findById(command.articleId) }
        val writerDeferred = coroutineScope.async { writerProvider.getWriter(command.writerId) }
        val categoryDeferred = command.categoryId?.let { coroutineScope.async { categoryRepository.findById(command.categoryId) } }
        return Triple(
            articleDeferred.await() ?: throw IllegalArgumentException("article not found"),
            writerDeferred.await() ?: throw IllegalArgumentException("writer not found"),
            if (command.categoryId != null) {
                categoryDeferred?.await() ?: throw IllegalArgumentException("category not found")
            } else {
                null
            },
        ).also { (article, writer, category) ->
            check(article.head == command.headVersion) { "article head version mismatch" }
            check(article.writerId == writer.id) { "article writer mismatch" }
            check(category?.id == article.categoryId) { "article category mismatch" }
        }
    }
}
