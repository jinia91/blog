package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.AuthorId

interface ISearchAllMemoByKeyword {
    fun handle(query: Query): Info

    data class Query(
        val requesterId: AuthorId,
        val keyword: String,
    )

    data class Info(val result: ResultInfo)

    data class ResultInfo(
        val name: String = "검색 결과",
        var memos: List<MemoInfo>,
    )

    data class MemoInfo(
        val id: Long,
        val title: String,
    )
}
