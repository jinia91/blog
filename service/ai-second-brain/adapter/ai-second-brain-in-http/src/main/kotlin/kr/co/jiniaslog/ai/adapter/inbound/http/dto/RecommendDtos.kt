package kr.co.jiniaslog.ai.adapter.inbound.http.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "메모 추천 응답")
data class RecommendedMemoResponse(
    @Schema(description = "메모 ID")
    val memoId: Long,
    @Schema(description = "메모 제목")
    val title: String,
    @Schema(description = "내용 미리보기")
    val contentPreview: String,
    @Schema(description = "유사도 점수 (0~1)")
    val similarity: Double,
)

@Schema(description = "임베딩 동기화 응답")
data class SyncResponse(
    @Schema(description = "동기화된 메모 수")
    val syncedCount: Int,
    @Schema(description = "동기화 메시지")
    val message: String,
)
