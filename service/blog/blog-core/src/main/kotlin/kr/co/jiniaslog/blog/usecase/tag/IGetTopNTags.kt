package kr.co.jiniaslog.blog.usecase.tag

import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName

interface IGetTopNTags {
    fun handle(query: Query): Info

    data class Query(
        val n: Int
    )

    data class Info(
        val tags: Map<TagId, TagName>
    )
}
