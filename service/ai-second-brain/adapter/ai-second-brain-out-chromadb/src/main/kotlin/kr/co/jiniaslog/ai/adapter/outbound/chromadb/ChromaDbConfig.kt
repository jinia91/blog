package kr.co.jiniaslog.ai.adapter.outbound.chromadb

import org.springframework.ai.chroma.ChromaApi
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.ChromaVectorStore
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChromaDbConfig {

    @Value("\${spring.ai.vectorstore.chroma.url:http://localhost:8000}")
    private lateinit var chromaUrl: String

    @Value("\${spring.ai.vectorstore.chroma.collection-name:memo_embeddings}")
    private lateinit var collectionName: String

    @Bean
    fun chromaApi(): ChromaApi {
        return ChromaApi(chromaUrl)
    }

    @Bean
    fun vectorStore(chromaApi: ChromaApi, embeddingModel: EmbeddingModel): VectorStore {
        return ChromaVectorStore(embeddingModel, chromaApi, collectionName, true)
    }
}
