package kr.co.jiniaslog.blog.queries

import kr.co.jiniaslog.blog.domain.article.ArticleVo
import kr.co.jiniaslog.blog.domain.tag.TagId

/**
 * 간소화 시킨 게시물들을 가져온다
 *
 * - 키워드 방식의 경우 커서를 사용할 수 없다.
 * - 커서 방식의 경우 커서기반으로 id를 내림차순으로 정렬하여 조회한다.
 */
interface IGetSimpleArticles {
    fun handle(query: Query): Info

    data class Query(
        val cursor: Long?,
        val limit: Int?,
        val isPublished: Boolean,
        val keyword: String? = null,
        private val tagIdRaw: Long? = null
    ) {
        val tagId: TagId? = tagIdRaw?.let { TagId(it) }

        fun isCursorQuery(): Boolean = cursor != null && limit != null && keyword == null && tagId == null
        fun isKeywordQuery(): Boolean = keyword != null && cursor == null && limit == null && tagId == null

        fun isTagQuery(): Boolean = tagId != null && cursor == null && limit == null && keyword == null
    }

    data class Info(
        val articles: List<ArticleVo>
    )
}
