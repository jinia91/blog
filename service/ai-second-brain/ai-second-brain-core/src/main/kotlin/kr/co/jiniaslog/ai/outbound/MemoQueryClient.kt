package kr.co.jiniaslog.ai.outbound

data class MemoInfo(
    val id: Long,
    val authorId: Long,
    val title: String,
    val content: String,
)

interface MemoQueryClient {
    fun getMemoById(memoId: Long): MemoInfo?
    fun getAllMemosByAuthorId(authorId: Long): List<MemoInfo>
}
