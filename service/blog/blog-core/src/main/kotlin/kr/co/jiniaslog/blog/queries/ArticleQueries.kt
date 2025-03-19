package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.outbound.ArticleRepository
import org.springframework.stereotype.Component

@Component
class ArticleQueries(
    private val articleRepository: ArticleRepository
) : ArticleQueriesFacade {
    override fun handle(query: IGetArticleById.Query): IGetArticleById.Info {
        val article = articleRepository.findById(query.articleId) ?: throw IllegalArgumentException("해당 아티클이 존재하지 않습니다")
        return IGetArticleById.Info(
            id = article.id,
            title = article.articleContents.title,
            content = article.articleContents.contents,
            thumbnailUrl = article.articleContents.thumbnailUrl,
            tags = article.tagsInfo,
            createdAt = article.createdAt!!,
            isPublished = article.isPublished
        )
    }

    override fun handle(query: IGetSimpleArticleListWithCursor.Query): List<IGetSimpleArticleListWithCursor.Info> {
        return articleRepository.getSimpleArticleListWithCursor(query.cursor, query.limit)
    }
}
