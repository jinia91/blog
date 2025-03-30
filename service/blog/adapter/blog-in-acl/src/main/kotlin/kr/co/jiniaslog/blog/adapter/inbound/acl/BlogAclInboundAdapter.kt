package kr.co.jiniaslog.blog.adapter.inbound.acl

import kr.co.jiniaslog.blog.queries.ArticleQueriesFacade
import kr.co.jiniaslog.blog.queries.IGetSimpleArticles
import org.springframework.stereotype.Component

@Component
class BlogAclInboundAdapter(
    private val articleQueriesFacade: ArticleQueriesFacade
) {
    fun getAllArticle(): List<ArticleAclVo> {
        val simpleAllArticles = articleQueriesFacade.handle(
            IGetSimpleArticles.Query(
                isPublished = true,
                cursor = null,
                limit = null,
                keyword = null,
                tagName = null
            )
        )

        return simpleAllArticles.articles.map {
            ArticleAclVo(
                id = it.id,
                title = it.title,
                content = it.content,
                createdAt = it.createdAt
            )
        }
    }
}
