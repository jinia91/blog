package kr.co.jiniaslog.ai.usecase

import kr.co.jiniaslog.ai.domain.agent.AgentOrchestrator
import kr.co.jiniaslog.ai.domain.agent.AgentResponse
import kr.co.jiniaslog.ai.domain.chat.AuthorId
import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageRepository
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.ChatSessionRepository
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import kr.co.jiniaslog.ai.outbound.EmbeddingStore
import kr.co.jiniaslog.ai.outbound.MemoEmbeddingDocument
import kr.co.jiniaslog.ai.outbound.MemoQueryClient
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import org.springframework.transaction.annotation.Transactional

private val logger = mu.KotlinLogging.logger {}

/**
 * AI 유스케이스 파사드 - Multi-Agent 아키텍처 기반
 *
 * AgentOrchestrator를 사용하여 토큰 최적화된 Multi-Agent 처리를 수행합니다.
 * - Intent Router (경량 모델): 의도 분류
 * - RAG Agent (풀 모델): 질문 답변
 * - Memo Management Agent (중간 모델): 메모/폴더 관리
 */
@UseCaseInteractor
class AiUseCasesFacade(
    private val chatSessionRepository: ChatSessionRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val embeddingStore: EmbeddingStore,
    private val agentOrchestrator: AgentOrchestrator,
    private val memoQueryClient: MemoQueryClient,
) : IChat,
    ICreateChatSession,
    IGetChatSessions,
    IGetChatHistory,
    IDeleteChatSession,
    IRecommendRelatedMemos,
    ISyncMemoToEmbedding,
    ISyncAllMemosToEmbedding,
    IDeleteMemoEmbedding {

    override fun invoke(command: IChat.Command): IChat.Info {
        val sessionId = ChatSessionId(command.sessionId)
        val authorId = AuthorId(command.authorId)

        val session = chatSessionRepository.findById(sessionId)
            ?: throw IllegalArgumentException("Session not found: ${command.sessionId}")

        require(session.authorId == authorId) { "Not authorized to access this session" }

        // 사용자 메시지 저장
        val userMessage = ChatMessage.create(sessionId, MessageRole.USER, command.message)
        chatMessageRepository.save(userMessage)

        // AgentOrchestrator를 통한 처리
        val response = agentOrchestrator.process(
            message = command.message,
            sessionId = sessionId.value,
            authorId = authorId.value
        )

        // 응답에 따른 처리
        val (responseMessage, createdMemoId) = when (response) {
            is AgentResponse.ChatResponse -> Pair(response.content, null)
            is AgentResponse.MemoCreated -> Pair(response.message, response.memoId)
            is AgentResponse.MemoUpdated -> Pair(response.message, null)
            is AgentResponse.MemoMoved -> Pair(response.message, null)
            is AgentResponse.FolderCreated -> Pair(response.message, null)
            is AgentResponse.FolderRenamed -> Pair(response.message, null)
            is AgentResponse.FolderMoved -> Pair(response.message, null)
            is AgentResponse.MemoList -> Pair(response.message, null)
            is AgentResponse.FolderList -> Pair(response.message, null)
            is AgentResponse.Deleted -> Pair(response.message, null)
            is AgentResponse.Error -> Pair(response.message, null)
        }

        val assistantMessage = ChatMessage.create(sessionId, MessageRole.ASSISTANT, responseMessage)
        chatMessageRepository.save(assistantMessage)

        return IChat.Info(
            sessionId = sessionId.value,
            response = responseMessage,
            createdMemoId = createdMemoId,
        )
    }

    override fun invoke(command: ICreateChatSession.Command): ICreateChatSession.Info {
        val session = ChatSession.create(
            authorId = AuthorId(command.authorId),
            title = command.title ?: ChatSession.DEFAULT_TITLE,
        )
        val saved = chatSessionRepository.save(session)
        return ICreateChatSession.Info(
            sessionId = saved.entityId.value,
            title = saved.title,
        )
    }

    override fun invoke(query: IGetChatSessions.Query): IGetChatSessions.PagedSessions {
        val sessions = chatSessionRepository.findByAuthorIdWithCursor(
            AuthorId(query.authorId),
            query.cursor?.let { ChatSessionId(it) },
            query.size + 1
        )

        val hasNext = sessions.size > query.size
        val resultSessions = if (hasNext) sessions.dropLast(1) else sessions
        val nextCursor = if (hasNext) resultSessions.lastOrNull()?.entityId?.value else null

        val sessionIds = resultSessions.map { it.entityId }
        val lastMessages = chatMessageRepository.findLastUserMessagesBySessionIds(sessionIds)

        return IGetChatSessions.PagedSessions(
            sessions = resultSessions.map { session ->
                IGetChatSessions.SessionInfo(
                    sessionId = session.entityId.value,
                    lastMessage = lastMessages[session.entityId]?.content?.take(100),
                    createdAt = session.createdAt,
                    updatedAt = session.updatedAt,
                )
            },
            nextCursor = nextCursor,
            hasNext = hasNext,
        )
    }

    override fun invoke(query: IGetChatHistory.Query): IGetChatHistory.PagedMessages {
        val session = chatSessionRepository.findById(ChatSessionId(query.sessionId))
            ?: throw IllegalArgumentException("Session not found: ${query.sessionId}")

        require(session.authorId == AuthorId(query.authorId)) { "Not authorized to access this session" }

        val cursorId = query.cursor?.let { kr.co.jiniaslog.ai.domain.chat.ChatMessageId(it) }
        val messages = chatMessageRepository.findBySessionIdWithCursor(
            ChatSessionId(query.sessionId),
            cursorId,
            query.size
        )

        // size+1개 조회 후 hasNext 판단
        val hasNext = messages.size > query.size
        val resultMessages = if (hasNext) messages.dropLast(1) else messages
        val nextCursor = if (hasNext) messages[query.size - 1].entityId.value else null

        return IGetChatHistory.PagedMessages(
            messages = resultMessages.map { message ->
                IGetChatHistory.MessageInfo(
                    messageId = message.entityId.value,
                    role = message.role,
                    content = message.content,
                    createdAt = message.createdAt,
                )
            },
            nextCursor = nextCursor,
            hasNext = hasNext,
        )
    }

    @Transactional("aiTransactionManager")
    override fun invoke(command: IDeleteChatSession.Command) {
        val sessionId = ChatSessionId(command.sessionId)
        val authorId = AuthorId(command.authorId)

        val session = chatSessionRepository.findById(sessionId)
            ?: throw IllegalArgumentException("Session not found: ${command.sessionId}")

        require(session.authorId == authorId) { "Not authorized to delete this session" }

        chatMessageRepository.deleteAllBySessionId(sessionId)
        chatSessionRepository.delete(session)
    }

    override fun invoke(query: IRecommendRelatedMemos.Query): List<IRecommendRelatedMemos.RecommendedMemo> {
        val searchQuery = when {
            query.query != null -> query.query
            query.currentMemoId != null -> {
                val memo = memoQueryClient.getMemoById(query.currentMemoId)
                    ?: throw IllegalArgumentException("Memo not found: ${query.currentMemoId}")
                "${memo.title} ${memo.content}"
            }
            else -> throw IllegalArgumentException("Either query or currentMemoId must be provided")
        }

        return embeddingStore.searchSimilar(searchQuery, query.authorId, query.topK)
            .filter { it.memoId != query.currentMemoId }
            .map { similarMemo ->
                IRecommendRelatedMemos.RecommendedMemo(
                    memoId = similarMemo.memoId,
                    title = similarMemo.title,
                    contentPreview = similarMemo.content.take(200),
                    similarity = similarMemo.similarity,
                )
            }
    }

    override fun invoke(command: ISyncMemoToEmbedding.Command) {
        embeddingStore.store(
            MemoEmbeddingDocument(
                memoId = command.memoId,
                authorId = command.authorId,
                title = command.title,
                content = command.content,
            )
        )
    }

    override fun invoke(command: ISyncAllMemosToEmbedding.Command): ISyncAllMemosToEmbedding.Info {
        val memos = memoQueryClient.getAllMemosByAuthorId(command.authorId)
        val documents = memos.map { memo ->
            MemoEmbeddingDocument(
                memoId = memo.id,
                authorId = memo.authorId,
                title = memo.title,
                content = memo.content,
            )
        }
        embeddingStore.storeAll(documents)
        return ISyncAllMemosToEmbedding.Info(syncedCount = documents.size)
    }

    override fun invoke(command: IDeleteMemoEmbedding.Command) {
        embeddingStore.delete(command.memoId)
    }
}
