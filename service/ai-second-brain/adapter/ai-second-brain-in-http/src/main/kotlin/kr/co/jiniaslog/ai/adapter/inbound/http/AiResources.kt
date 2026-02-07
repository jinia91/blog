package kr.co.jiniaslog.ai.adapter.inbound.http

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.ChatHistoryResponse
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.ChatRequest
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.ChatResponse
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.CreateSessionRequest
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.CreateSessionResponse
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.MessageResponse
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.RecommendedMemoResponse
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.SessionResponse
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.SessionsPageResponse
import kr.co.jiniaslog.ai.adapter.inbound.http.dto.SyncResponse
import kr.co.jiniaslog.ai.usecase.IChat
import kr.co.jiniaslog.ai.usecase.ICreateChatSession
import kr.co.jiniaslog.ai.usecase.IDeleteChatSession
import kr.co.jiniaslog.ai.usecase.IGetChatHistory
import kr.co.jiniaslog.ai.usecase.IGetChatSessions
import kr.co.jiniaslog.ai.usecase.IRecommendRelatedMemos
import kr.co.jiniaslog.ai.usecase.ISyncAllMemosToEmbedding
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/ai")
class AiResources(
    private val chatUseCase: IChat,
    private val createSessionUseCase: ICreateChatSession,
    private val getSessionsUseCase: IGetChatSessions,
    private val getChatHistoryUseCase: IGetChatHistory,
    private val deleteSessionUseCase: IDeleteChatSession,
    private val recommendUseCase: IRecommendRelatedMemos,
    private val syncAllUseCase: ISyncAllMemosToEmbedding,
) {
    companion object {
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    @Tag(name = "Chat")
    @Operation(summary = "AI 챗봇과 대화", description = "RAG 기반으로 사용자의 메모를 참조하여 답변을 생성합니다")
    @PostMapping("/chat")
    fun chat(
        @AuthUserId userId: Long,
        @RequestBody request: ChatRequest,
    ): ResponseEntity<ChatResponse> {
        val result = chatUseCase(
            IChat.Command(
                sessionId = request.sessionId,
                authorId = userId,
                message = request.message,
            )
        )
        return ResponseEntity.ok(
            ChatResponse(
                sessionId = result.sessionId,
                response = result.response,
                createdMemoId = result.createdMemoId,
            )
        )
    }

    @Tag(name = "Chat")
    @Operation(summary = "새 채팅 세션 생성")
    @PostMapping("/sessions")
    fun createSession(
        @AuthUserId userId: Long,
        @RequestBody request: CreateSessionRequest,
    ): ResponseEntity<CreateSessionResponse> {
        val result = createSessionUseCase(
            ICreateChatSession.Command(
                authorId = userId,
                title = request.title,
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(
            CreateSessionResponse(
                sessionId = result.sessionId,
                title = result.title,
            )
        )
    }

    @Tag(name = "Chat")
    @Operation(summary = "사용자의 채팅 세션 목록 조회 (커서 페이징)")
    @GetMapping("/sessions")
    fun getSessions(
        @AuthUserId userId: Long,
        @Parameter(description = "커서 (세션 ID, null이면 처음부터)") @RequestParam(required = false) cursor: Long?,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<SessionsPageResponse> {
        val result = getSessionsUseCase(
            IGetChatSessions.Query(
                authorId = userId,
                cursor = cursor,
                size = size,
            )
        )
        return ResponseEntity.ok(
            SessionsPageResponse(
                sessions = result.sessions.map { session ->
                    SessionResponse(
                        sessionId = session.sessionId,
                        title = session.lastMessage ?: "새 대화",
                        createdAt = session.createdAt?.format(formatter),
                        updatedAt = session.updatedAt?.format(formatter),
                    )
                },
                nextCursor = result.nextCursor,
                hasNext = result.hasNext,
            )
        )
    }

    @Tag(name = "Chat")
    @Operation(summary = "채팅 세션 삭제")
    @DeleteMapping("/sessions/{sessionId}")
    fun deleteSession(
        @AuthUserId userId: Long,
        @Parameter(description = "세션 ID") @PathVariable sessionId: Long,
    ): ResponseEntity<Unit> {
        deleteSessionUseCase(
            IDeleteChatSession.Command(
                sessionId = sessionId,
                authorId = userId,
            )
        )
        return ResponseEntity.noContent().build()
    }

    @Tag(name = "Chat")
    @Operation(summary = "특정 세션의 채팅 히스토리 조회 (커서 페이징)")
    @GetMapping("/sessions/{sessionId}/messages")
    fun getChatHistory(
        @AuthUserId userId: Long,
        @Parameter(description = "세션 ID") @PathVariable sessionId: Long,
        @Parameter(description = "커서 (메시지 ID, null이면 처음부터)") @RequestParam(required = false) cursor: Long?,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<ChatHistoryResponse> {
        val result = getChatHistoryUseCase(
            IGetChatHistory.Query(
                sessionId = sessionId,
                authorId = userId,
                cursor = cursor,
                size = size,
            )
        )
        return ResponseEntity.ok(
            ChatHistoryResponse(
                messages = result.messages.map { message ->
                    MessageResponse(
                        messageId = message.messageId,
                        role = message.role,
                        content = message.content,
                        createdAt = message.createdAt?.format(formatter),
                    )
                },
                nextCursor = result.nextCursor,
                hasNext = result.hasNext,
            )
        )
    }

    @Tag(name = "Recommend")
    @Operation(summary = "관련 메모 추천", description = "쿼리 또는 현재 메모와 유사한 메모를 추천합니다")
    @GetMapping("/recommend")
    fun recommendMemos(
        @AuthUserId userId: Long,
        @Parameter(description = "검색 쿼리") @RequestParam(required = false) query: String?,
        @Parameter(description = "현재 메모 ID") @RequestParam(required = false) memoId: Long?,
        @Parameter(description = "추천 개수") @RequestParam(defaultValue = "5") topK: Int,
    ): ResponseEntity<List<RecommendedMemoResponse>> {
        val recommendations = recommendUseCase(
            IRecommendRelatedMemos.Query(
                authorId = userId,
                query = query,
                currentMemoId = memoId,
                topK = topK,
            )
        )
        return ResponseEntity.ok(
            recommendations.map { rec ->
                RecommendedMemoResponse(
                    memoId = rec.memoId,
                    title = rec.title,
                    contentPreview = rec.contentPreview,
                    similarity = rec.similarity,
                )
            }
        )
    }

    @Tag(name = "Sync")
    @Operation(summary = "모든 메모를 임베딩 저장소에 동기화")
    @PostMapping("/sync")
    fun syncAllMemos(
        @AuthUserId userId: Long,
    ): ResponseEntity<SyncResponse> {
        val result = syncAllUseCase(
            ISyncAllMemosToEmbedding.Command(authorId = userId)
        )
        return ResponseEntity.ok(
            SyncResponse(
                syncedCount = result.syncedCount,
                message = "${result.syncedCount}개의 메모가 동기화되었습니다.",
            )
        )
    }
}
