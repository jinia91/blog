package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.ArticleSearcher
import org.springframework.stereotype.Component

@Component
class ArticleQueries(
    private val articleRepository: ArticleRepository,
    private val articleSearcher: ArticleSearcher
) : ArticleQueriesFacade {
    override fun handle(query: IGetArticleById.Query): IGetArticleById.Info {
        val article = articleRepository.findById(query.articleId) ?: throw IllegalArgumentException("해당 아티클이 존재하지 않습니다")
        val contents = if (query.isDraft) article.draftContents else article.articleContents

        return IGetArticleById.Info(
            id = article.id,
            title = contents.title,
            content = contents.contents,
            thumbnailUrl = contents.thumbnailUrl,
            tags = article.tagsInfo,
            createdAt = article.createdAt!!,
            isPublished = article.isPublished
        )
    }

    override fun handle(query: IGetPublishedSimpleArticleListWithCursor.Query): List<IGetPublishedSimpleArticleListWithCursor.Info> {
        return articleRepository.getSimpleArticleListWithCursor(query.cursor, query.limit, query.isPublished)
    }

    override fun handle(query: IGetPublishedArticleByKeyword.Query): IGetPublishedArticleByKeyword.Info {
        return IGetPublishedArticleByKeyword.Info(
            articles = articleSearcher.searchPublishedArticlesByKeyword(query.keyword)
        )
    }
}
