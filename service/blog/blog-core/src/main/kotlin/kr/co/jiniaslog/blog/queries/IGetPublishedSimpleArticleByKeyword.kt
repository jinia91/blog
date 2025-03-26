package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.ArticleVo

/**
 * 키워드로 발행된 게시글을 가져온다
 *
 * 게시글 특성상 대량의 자료가 아니므로 페이징 처리는 하지 않는다
 *
 */
interface IGetPublishedSimpleArticleByKeyword {
    fun handle(query: Query): Info

    data class Query(
        val keyword: String,
    )

    data class Info(val articles: List<ArticleVo>)
}
