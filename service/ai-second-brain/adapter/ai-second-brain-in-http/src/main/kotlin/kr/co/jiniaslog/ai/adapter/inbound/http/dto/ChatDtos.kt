package kr.co.jiniaslog.ai.adapter.inbound.http.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.jiniaslog.ai.domain.chat.MessageRole

@Schema(description = "채팅 요청")
data class ChatRequest(
    @Schema(description = "세션 ID", example = "1234567890")
    val sessionId: Long,
    @Schema(description = "사용자 메시지", example = "오늘 배운 내용을 정리해줘")
    val message: String,
)

@Schema(description = "채팅 응답")
data class ChatResponse(
    @Schema(description = "세션 ID")
    val sessionId: Long,
    @Schema(description = "AI 응답 메시지")
    val response: String,
    @Schema(description = "생성된 메모 ID (메모 생성 시에만)")
    val createdMemoId: Long?,
)

@Schema(description = "세션 생성 요청")
data class CreateSessionRequest(
    @Schema(description = "세션 제목", example = "새 대화")
    val title: String? = null,
)

@Schema(description = "세션 생성 응답")
data class CreateSessionResponse(
    @Schema(description = "세션 ID")
    val sessionId: Long,
    @Schema(description = "세션 제목")
    val title: String,
)

@Schema(description = "세션 정보")
data class SessionResponse(
    @Schema(description = "세션 ID")
    val sessionId: Long,
    @Schema(description = "세션 제목")
    val title: String,
    @Schema(description = "생성일시")
    val createdAt: String?,
    @Schema(description = "수정일시")
    val updatedAt: String?,
)

@Schema(description = "채팅 메시지")
data class MessageResponse(
    @Schema(description = "메시지 ID")
    val messageId: Long,
    @Schema(description = "역할 (USER, ASSISTANT, SYSTEM)")
    val role: MessageRole,
    @Schema(description = "메시지 내용")
    val content: String,
    @Schema(description = "생성일시")
    val createdAt: String?,
)

@Schema(description = "채팅 히스토리 응답 (페이징)")
data class ChatHistoryResponse(
    @Schema(description = "메시지 목록")
    val messages: List<MessageResponse>,
    @Schema(description = "다음 페이지 커서 (null이면 마지막 페이지)")
    val nextCursor: Long?,
    @Schema(description = "다음 페이지 존재 여부")
    val hasNext: Boolean,
)

@Schema(description = "세션 목록 응답 (페이징)")
data class SessionsPageResponse(
    @Schema(description = "세션 목록")
    val sessions: List<SessionResponse>,
    @Schema(description = "다음 페이지 커서 (null이면 마지막 페이지)")
    val nextCursor: Long?,
    @Schema(description = "다음 페이지 존재 여부")
    val hasNext: Boolean,
)
