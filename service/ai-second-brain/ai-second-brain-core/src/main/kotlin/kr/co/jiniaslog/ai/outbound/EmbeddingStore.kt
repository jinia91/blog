package kr.co.jiniaslog.ai.outbound

data class MemoEmbeddingDocument(
    val memoId: Long,
    val authorId: Long,
    val title: String,
    val content: String,
)

data class SimilarMemo(
    val memoId: Long,
    val title: String,
    val content: String,
    val similarity: Double,
)

interface EmbeddingStore {
    fun store(document: MemoEmbeddingDocument)
    fun storeAll(documents: List<MemoEmbeddingDocument>)
    fun delete(memoId: Long)
    fun searchSimilar(query: String, authorId: Long, topK: Int = 5): List<SimilarMemo>
}
