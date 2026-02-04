package kr.co.jiniaslog.ai.adapter.outbound.chromadb

import kr.co.jiniaslog.ai.outbound.EmbeddingStore
import kr.co.jiniaslog.ai.outbound.MemoEmbeddingDocument
import kr.co.jiniaslog.ai.outbound.SimilarMemo
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.context.annotation.Lazy

@PersistenceAdapter
class ChromaEmbeddingStoreAdapter(
    @Lazy private val vectorStore: VectorStore,
) : EmbeddingStore {

    companion object {
        private const val MEMO_ID_KEY = "memoId"
        private const val AUTHOR_ID_KEY = "authorId"
        private const val TITLE_KEY = "title"
    }

    override fun store(document: MemoEmbeddingDocument) {
        val doc = Document.builder()
            .id("${document.memoId}")
            .text("${document.title}\n\n${document.content}")
            .metadata(
                mapOf(
                    MEMO_ID_KEY to document.memoId.toString(),
                    AUTHOR_ID_KEY to document.authorId.toString(),
                    TITLE_KEY to document.title,
                )
            )
            .build()
        vectorStore.add(listOf(doc))
    }

    override fun storeAll(documents: List<MemoEmbeddingDocument>) {
        val docs = documents.map { document ->
            Document.builder()
                .id("${document.memoId}")
                .text("${document.title}\n\n${document.content}")
                .metadata(
                    mapOf(
                        MEMO_ID_KEY to document.memoId.toString(),
                        AUTHOR_ID_KEY to document.authorId.toString(),
                        TITLE_KEY to document.title,
                    )
                )
                .build()
        }
        vectorStore.add(docs)
    }

    override fun delete(memoId: Long) {
        vectorStore.delete(listOf("$memoId"))
    }

    override fun searchSimilar(query: String, authorId: Long, topK: Int): List<SimilarMemo> {
        val request = SearchRequest.builder()
            .query(query)
            .topK(topK * 2)
            .filterExpression("$AUTHOR_ID_KEY == '$authorId'")
            .build()

        return vectorStore.similaritySearch(request)
            .take(topK)
            .map { doc: Document ->
                SimilarMemo(
                    memoId = (doc.metadata[MEMO_ID_KEY] as String).toLong(),
                    title = doc.metadata[TITLE_KEY] as String,
                    content = doc.text ?: "",
                    similarity = 0.0,
                )
            }
    }
}
