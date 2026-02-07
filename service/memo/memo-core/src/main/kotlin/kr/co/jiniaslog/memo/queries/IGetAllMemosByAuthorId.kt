package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.domain.memo.AuthorId

interface IGetAllMemosByAuthorId {
    fun handle(query: Query): List<MemoInfo>

    data class Query(
        val authorId: AuthorId,
    )

    data class MemoInfo(
        val id: Long,
        val authorId: Long,
        val title: String,
        val content: String,
    )
}
