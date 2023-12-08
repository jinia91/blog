package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName

interface IGetAllTags {
    fun handle(query: Query): List<Info>

    class Query()

    data class Info(
        val tagId: TagId,
        val name: TagName,
    )
}
