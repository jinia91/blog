// package kr.co.jiniaslog.ai.adapter.outbound.chromadb
//
// import com.fasterxml.jackson.databind.ObjectMapper
// import org.springframework.ai.chroma.vectorstore.ChromaApi
// import org.springframework.ai.chroma.vectorstore.ChromaVectorStore
// import org.springframework.ai.embedding.EmbeddingModel
// import org.springframework.ai.vectorstore.VectorStore
// import org.springframework.beans.factory.annotation.Value
// import org.springframework.context.annotation.Bean
// import org.springframework.context.annotation.Configuration
// import org.springframework.context.annotation.Lazy
// import org.springframework.web.client.RestClient
//
// @Configuration
// class ChromaDbConfig {
//
//    @Value("\${spring.ai.vectorstore.chroma.url:http://localhost:8000}")
//    private lateinit var chromaUrl: String
//
//    @Value("\${spring.ai.vectorstore.chroma.collection-name:memo_embeddings}")
//    private lateinit var collectionName: String
//
//    @Value("\${spring.ai.vectorstore.chroma.initialize-schema:true}")
//    private var initializeSchema: Boolean = true
//
//    @Bean
//    fun chromaApi(objectMapper: ObjectMapper): ChromaApi {
//        return ChromaApi(chromaUrl, RestClient.builder(), objectMapper)
//    }
//
//    @Bean
//    @Lazy
//    fun vectorStore(chromaApi: ChromaApi, embeddingModel: EmbeddingModel): VectorStore {
//        return ChromaVectorStore.builder(chromaApi, embeddingModel)
//            .collectionName(collectionName)
//            .initializeSchema(initializeSchema)
//            .build()
//    }
// }
