package kr.co.jiniaslog.memo.queries

interface ISearchAllFoldersAndMemo {
    fun handle(query: Query): Info

    data class Query(
        val requesterId: Long,
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
