package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleContent
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleThumbnailUrl
import kr.co.jiniaslog.blog.domain.article.ArticleTitle
import kr.co.jiniaslog.blog.domain.category.CategoryId

interface ArticleQueries {
    suspend fun getArticle(articleId: ArticleId): ArticleInfo

    data class ArticleInfo(
        val articleId: ArticleId,
        val title: ArticleTitle?,
        val content: ArticleContent,
        val categoryId: CategoryId?,
        val thumbnailUrl: ArticleThumbnailUrl?,
    )
}
