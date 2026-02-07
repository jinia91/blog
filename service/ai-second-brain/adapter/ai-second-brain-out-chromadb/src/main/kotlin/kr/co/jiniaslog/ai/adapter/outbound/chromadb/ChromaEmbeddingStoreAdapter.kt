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
        private const val CHUNK_SIZE = 1500
        private const val CHUNK_OVERLAP = 200
        private const val MAX_CHUNK_COUNT = 50
    }

    override fun store(document: MemoEmbeddingDocument) {
        delete(document.memoId)

        val fullText = "${document.title}\n\n${document.content}"
        val chunks = chunkText(fullText)

        val docs = chunks.mapIndexed { index, chunk ->
            Document.builder()
                .id("${document.memoId}_$index")
                .text(chunk)
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

    override fun storeAll(documents: List<MemoEmbeddingDocument>) {
        val docs = documents.flatMap { document ->
            val fullText = "${document.title}\n\n${document.content}"
            val chunks = chunkText(fullText)

            chunks.mapIndexed { index, chunk ->
                Document.builder()
                    .id("${document.memoId}_$index")
                    .text(chunk)
                    .metadata(
                        mapOf(
                            MEMO_ID_KEY to document.memoId.toString(),
                            AUTHOR_ID_KEY to document.authorId.toString(),
                            TITLE_KEY to document.title,
                        )
                    )
                    .build()
            }
        }
        vectorStore.add(docs)
    }

    override fun delete(memoId: Long) {
        val idsToDelete = mutableListOf("$memoId")
        for (index in 0 until MAX_CHUNK_COUNT) {
            idsToDelete.add("${memoId}_$index")
        }
        vectorStore.delete(idsToDelete)
    }

    override fun searchSimilar(query: String, authorId: Long, topK: Int): List<SimilarMemo> {
        val request = SearchRequest.builder()
            .query(query)
            .topK(topK * 3)
            .filterExpression("$AUTHOR_ID_KEY == '$authorId'")
            .build()

        return vectorStore.similaritySearch(request)
            .groupBy { doc -> (doc.metadata[MEMO_ID_KEY] as String).toLong() }
            .mapNotNull { (memoId, chunks) ->
                // Pick the chunk with highest similarity
                val bestChunk = chunks.maxByOrNull { it.score ?: 0.0 } ?: return@mapNotNull null
                SimilarMemo(
                    memoId = memoId,
                    title = bestChunk.metadata[TITLE_KEY] as String,
                    content = bestChunk.text ?: "",
                    similarity = bestChunk.score ?: 0.0,
                )
            }
            .sortedByDescending { it.similarity }
            .take(topK)
    }

    private fun chunkText(text: String): List<String> {
        if (text.length <= CHUNK_SIZE) {
            return listOf(text)
        }

        val chunks = mutableListOf<String>()
        var startIndex = 0

        while (startIndex < text.length) {
            val endIndex = minOf(startIndex + CHUNK_SIZE, text.length)
            chunks.add(text.substring(startIndex, endIndex))

            if (endIndex >= text.length) break

            startIndex = endIndex - CHUNK_OVERLAP
        }

        return chunks
    }
}
