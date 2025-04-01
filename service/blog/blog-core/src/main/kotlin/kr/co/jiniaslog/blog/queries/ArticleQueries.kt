package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.ArticleSearcher
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class ArticleQueries(
    private val articleRepository: ArticleRepository,
    private val articleSearcher: ArticleSearcher
) : ArticleQueriesFacade {
    override fun handle(query: IGetArticleById.Query): IGetArticleById.Info {
        val article = articleRepository.findById(query.articleId) ?: throw IllegalArgumentException("해당 아티클이 존재하지 않습니다")
        val contents = if (query.status === Article.Status.DRAFT) article.draftContents else article.articleContents

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

    @Cacheable("blog.article.simple", key = "#query")
    override fun handle(query: IGetSimpleArticles.Query): IGetSimpleArticles.Info {
        val vos = when {
            query.isKeywordQuery() -> articleSearcher.searchPublishedArticlesByKeyword(query.keyword!!)
            query.isCursorQuery() -> articleRepository.getArticleListWithCursor(
                query.cursor!!,
                query.limit!!,
                query.isPublished
            )
            query.isTagQuery() -> articleRepository.getArticleByTagName(TagName(query.tagName!!))
            query.isJustSimplePublishedQuery() -> articleRepository.findArticleVoByStatus(Article.Status.PUBLISHED)
            else -> throw IllegalArgumentException("지원하지 않는 쿼리 입니다")
        }
        return IGetSimpleArticles.Info(vos.map { it.toSimplifiedArticleVo() })
    }
}
