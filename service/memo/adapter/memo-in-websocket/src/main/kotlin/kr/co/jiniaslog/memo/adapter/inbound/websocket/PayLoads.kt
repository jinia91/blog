package kr.co.jiniaslog.memo.adapter.inbound.websocket

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents
import kr.co.jiniaslog.memo.usecase.IUpdateMemoReferences

data class UpdateMemoPayload(
    val type: String = "UpdateMemo",
    val id: Long,
    @field:NotEmpty
    val content: String,
    @field:NotEmpty
    val title: String,
) {
    fun toCommand(): IUpdateMemoContents.Command {
        return IUpdateMemoContents.Command(
            content = MemoContent(content),
            title = MemoTitle(title),
            memoId = MemoId(id),
        )
    }
}

class UpdateMemoResponse @JsonCreator constructor(
    @JsonProperty("id")
    val id: Long,
) {
    companion object {
        fun from(id: Long): UpdateMemoResponse {
            return UpdateMemoResponse(
                id = id,
            )
        }
    }
}

fun IUpdateMemoContents.Info.toResponse(): UpdateMemoResponse {
    return UpdateMemoResponse.from(id.value)
}

data class UpdateReferencesPayload(
    val type: String = "UpdateReferences",
    val id: Long,
    val references: List<Long>,
) {
    fun toCommand(): IUpdateMemoReferences.Command {
        return IUpdateMemoReferences.Command.UpdateReferences(
            memoId = MemoId(id),
            references = references.map { MemoId(it) }.toSet(),
        )
    }
}

data class UpdateReferencesResponse @JsonCreator constructor(
    @JsonProperty("id")
    val id: Long,
) {
    companion object {
        fun from(id: Long): UpdateReferencesResponse {
            return UpdateReferencesResponse(
                id = id,
            )
        }
    }
}

fun IUpdateMemoReferences.Info.toResponse(): UpdateReferencesResponse {
    return UpdateReferencesResponse.from(id.value)
}
