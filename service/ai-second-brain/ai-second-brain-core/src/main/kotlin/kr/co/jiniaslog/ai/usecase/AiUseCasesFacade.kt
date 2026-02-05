package kr.co.jiniaslog.ai.usecase

import kr.co.jiniaslog.ai.domain.chat.AuthorId
import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageRepository
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.ChatSessionRepository
import kr.co.jiniaslog.ai.domain.chat.MessageRole
import kr.co.jiniaslog.ai.outbound.ChatContext
import kr.co.jiniaslog.ai.outbound.EmbeddingStore
import kr.co.jiniaslog.ai.outbound.IntentType
import kr.co.jiniaslog.ai.outbound.LlmService
import kr.co.jiniaslog.ai.outbound.MemoCommandService
import kr.co.jiniaslog.ai.outbound.MemoEmbeddingDocument
import kr.co.jiniaslog.ai.outbound.MemoQueryService
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import org.springframework.transaction.annotation.Transactional

private val logger = mu.KotlinLogging.logger {}

@UseCaseInteractor
class AiUseCasesFacade(
    private val chatSessionRepository: ChatSessionRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val embeddingStore: EmbeddingStore,
    private val llmService: LlmService,
    private val memoQueryService: MemoQueryService,
    private val memoCommandService: MemoCommandService,
) : IChat,
    ICreateChatSession,
    IGetChatSessions,
    IGetChatHistory,
    IDeleteChatSession,
    IRecommendRelatedMemos,
    ISyncMemoToEmbedding,
    ISyncAllMemosToEmbedding,
    IDeleteMemoEmbedding {

    companion object {
        private const val SYSTEM_PROMPT = """당신은 사용자의 개인 지식 관리 시스템에서 작동하는 AI 어시스턴트입니다.
사용자의 메모 내용을 기반으로 질문에 답변하고, 필요한 경우 관련 메모를 참조하여 정확한 정보를 제공합니다.
답변은 명확하고 간결하게 제공하며, 참조한 메모가 있다면 언급해주세요. 메모가 없다면 솔직하게 모른다고 답변하세요."""
    }

    override fun invoke(command: IChat.Command): IChat.Info {
        val sessionId = ChatSessionId(command.sessionId)
        val authorId = AuthorId(command.authorId)

        val session = chatSessionRepository.findById(sessionId)
            ?: throw IllegalArgumentException("Session not found: ${command.sessionId}")

        require(session.authorId == authorId) { "Not authorized to access this session" }

        val intent = llmService.classifyIntent(command.message)

        return when (intent) {
            IntentType.MEMO_CREATION -> handleMemoCreation(sessionId, authorId, command.message)
            else -> handleChat(sessionId, authorId, command.message)
        }
    }

    private fun handleChat(
        sessionId: ChatSessionId,
        authorId: AuthorId,
        message: String,
    ): IChat.Info {
        val userMessage = ChatMessage.create(sessionId, MessageRole.USER, message)
        chatMessageRepository.save(userMessage)

        val history = chatMessageRepository.findAllBySessionId(sessionId)
        val relevantDocs = embeddingStore.searchSimilar(message, authorId.value, 5)
        logger.info { "Found ${relevantDocs.size} relevant documents for the message." }
        logger.info { relevantDocs }

        val context = ChatContext(
            systemPrompt = SYSTEM_PROMPT,
            conversationHistory = history,
            relevantDocuments = relevantDocs,
        )

        val response = llmService.chat(message, context)

        val assistantMessage = ChatMessage.create(sessionId, MessageRole.ASSISTANT, response)
        chatMessageRepository.save(assistantMessage)

        return IChat.Info(
            sessionId = sessionId.value,
            response = response,
            createdMemoId = null,
        )
    }

    private fun handleMemoCreation(
        sessionId: ChatSessionId,
        authorId: AuthorId,
        message: String,
    ): IChat.Info {
        val userMessage = ChatMessage.create(sessionId, MessageRole.USER, message)
        chatMessageRepository.save(userMessage)

        val memoId = memoCommandService.createMemo(
            authorId = authorId.value,
            title = extractTitle(message),
            content = message,
        )

        val response = "메모가 생성되었습니다. (ID: $memoId)"
        val assistantMessage = ChatMessage.create(sessionId, MessageRole.ASSISTANT, response)
        chatMessageRepository.save(assistantMessage)

        return IChat.Info(
            sessionId = sessionId.value,
            response = response,
            createdMemoId = memoId,
        )
    }

    private fun extractTitle(content: String): String {
        val firstLine = content.lines().firstOrNull()?.take(50) ?: "Untitled"
        return if (firstLine.isBlank()) "Untitled" else firstLine
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

    override fun invoke(query: IGetChatSessions.Query): List<IGetChatSessions.SessionInfo> {
        return chatSessionRepository.findAllByAuthorId(AuthorId(query.authorId))
            .map { session ->
                IGetChatSessions.SessionInfo(
                    sessionId = session.entityId.value,
                    title = session.title,
                    createdAt = session.createdAt,
                    updatedAt = session.updatedAt,
                )
            }
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
                val memo = memoQueryService.getMemoById(query.currentMemoId)
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
        val memos = memoQueryService.getAllMemosByAuthorId(command.authorId)
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
