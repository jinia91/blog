package kr.co.jiniaslog.ai.adapter.outbound.chromadb

import kr.co.jiniaslog.ai.outbound.EmbeddingStore
import kr.co.jiniaslog.ai.outbound.MemoEmbeddingDocument
import kr.co.jiniaslog.ai.outbound.SimilarMemo
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore

@PersistenceAdapter
class ChromaEmbeddingStoreAdapter(
    private val vectorStore: VectorStore,
) : EmbeddingStore {

    companion object {
        private const val MEMO_ID_KEY = "memoId"
        private const val AUTHOR_ID_KEY = "authorId"
        private const val TITLE_KEY = "title"
    }

    override fun store(document: MemoEmbeddingDocument) {
        val doc = Document(
            "${document.memoId}",
            "${document.title}\n\n${document.content}",
            mapOf(
                MEMO_ID_KEY to document.memoId,
                AUTHOR_ID_KEY to document.authorId,
                TITLE_KEY to document.title,
            )
        )
        vectorStore.add(listOf(doc))
    }

    override fun storeAll(documents: List<MemoEmbeddingDocument>) {
        val docs = documents.map { document ->
            Document(
                "${document.memoId}",
                "${document.title}\n\n${document.content}",
                mapOf(
                    MEMO_ID_KEY to document.memoId,
                    AUTHOR_ID_KEY to document.authorId,
                    TITLE_KEY to document.title,
                )
            )
        }
        vectorStore.add(docs)
    }

    override fun delete(memoId: Long) {
        vectorStore.delete(listOf("$memoId"))
    }

    override fun searchSimilar(query: String, authorId: Long, topK: Int): List<SimilarMemo> {
        val request = SearchRequest.defaults()
            .withQuery(query)
            .withTopK(topK * 2)
            .withFilterExpression("$AUTHOR_ID_KEY == $authorId")

        return vectorStore.similaritySearch(request)
            .take(topK)
            .map { doc: Document ->
                SimilarMemo(
                    memoId = (doc.metadata[MEMO_ID_KEY] as Number).toLong(),
                    title = doc.metadata[TITLE_KEY] as String,
                    content = doc.content ?: "",
                    similarity = 0.0,
                )
            }
    }
}
