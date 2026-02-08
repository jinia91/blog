package kr.co.jiniaslog.ai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer
import org.springframework.ai.rag.retrieval.search.DocumentRetriever
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Multi-Agent 아키텍처를 위한 ChatClient 설정
 *
 * 토큰 최적화 전략:
 * 1. lightweightChatClient: Intent Router용 - Advisor 없음, 단순 분류용
 * 2. ragChatClient: RAG Agent용 - RetrievalAugmentationAdvisor + MessageChatMemoryAdvisor
 * 3. memoChatClient: Memo Agent용 - Tool Calling 지원
 *
 * RAG 고도화:
 * - RetrievalAugmentationAdvisor: 통합 RAG 파이프라인 관리
 * - RewriteQueryTransformer: 검색 전 질문 재작성으로 정확도 향상
 * - VectorStoreDocumentRetriever: authorId 필터 적용한 문서 검색
 * - ContextualQueryAugmenter: 빈 컨텍스트 처리
 */
@Configuration
class AiConfig {

    /**
     * ChatMemory 빈 - 대화 히스토리 관리
     * InMemoryChatMemory를 사용하여 최근 N개 메시지 유지
     */
    @Bean
    fun chatMemory(): ChatMemory {
        return MessageWindowChatMemory.builder()
            .maxMessages(20)
            .build()
    }

    /**
     * Query Rewriting용 ChatClient (낮은 temperature로 정확한 재작성)
     */
    @Bean("queryRewriterChatClient")
    fun queryRewriterChatClient(
        @Qualifier("googleGenAiChatModel") chatModel: ChatModel
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .build()
    }

    /**
     * RewriteQueryTransformer - 질문 재작성으로 검색 정확도 향상
     */
    @Bean
    fun queryTransformer(
        @Qualifier("queryRewriterChatClient") chatClient: ChatClient
    ): QueryTransformer {
        return RewriteQueryTransformer.builder()
            .chatClientBuilder(chatClient.mutate())
            .build()
    }

    /**
     * VectorStoreDocumentRetriever - authorId 필터 적용한 문서 검색
     */
    @Bean
    fun documentRetriever(vectorStore: VectorStore): DocumentRetriever {
        return VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .similarityThreshold(0.3) // 낮춰서 더 많은 결과 검색
            .topK(5)
            .build()
    }

    /**
     * RetrievalAugmentationAdvisor - RAG 통합 파이프라인
     */
    @Bean
    fun retrievalAugmentationAdvisor(
        queryTransformer: QueryTransformer,
        documentRetriever: DocumentRetriever
    ): RetrievalAugmentationAdvisor {
        return RetrievalAugmentationAdvisor.builder()
            .queryTransformers(queryTransformer)
            .documentRetriever(documentRetriever)
            .queryAugmenter(
                ContextualQueryAugmenter.builder()
                    .allowEmptyContext(true) // 검색 결과 없어도 응답 생성
                    .build()
            )
            .build()
    }

    /**
     * 1. Intent Router용 경량 ChatClient (RAG/Memory 불필요)
     * 단순 의도 분류만 수행하므로 Advisor 없이 구성
     */
    @Bean("lightweightChatClient")
    fun lightweightChatClient(
        @Qualifier("googleGenAiChatModel") chatModel: ChatModel
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .build()
    }

    /**
     * 2. RAG Agent용 풀 ChatClient
     * RetrievalAugmentationAdvisor: 질문 재작성 + 문서 검색 + 컨텍스트 병합
     * MessageChatMemoryAdvisor: 대화 히스토리 자동 관리
     */
    @Bean("ragChatClient")
    @Primary
    fun ragChatClient(
        @Qualifier("googleGenAiChatModel") chatModel: ChatModel,
        chatMemory: ChatMemory,
        retrievalAugmentationAdvisor: RetrievalAugmentationAdvisor
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                retrievalAugmentationAdvisor
            )
            .build()
    }

    /**
     * 3. 일반 대화용 ChatClient (Memory만, RAG 없음)
     * GENERAL_CHAT 인텐트에서 사용 - RAG 노이즈 없이 대화 히스토리만 유지
     */
    @Bean("generalChatClient")
    fun generalChatClient(
        @Qualifier("googleGenAiChatModel") chatModel: ChatModel,
        chatMemory: ChatMemory
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build()
            )
            .build()
    }

    /**
     * 4. Memo Agent용 ChatClient (Tool Calling 지원)
     * Tools는 런타임에 주입됨
     */
    @Bean("memoChatClient")
    fun memoChatClient(
        @Qualifier("googleGenAiChatModel") chatModel: ChatModel
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .build()
    }

    /**
     * 기존 LlmServiceImpl 호환을 위한 기본 ChatClient
     * @deprecated AgentOrchestrator 사용 권장
     */
    @Bean("chatClient")
    fun chatClient(@Qualifier("googleGenAiChatModel") chatModel: ChatModel): ChatClient {
        return ChatClient.builder(chatModel)
            .build()
    }
}
