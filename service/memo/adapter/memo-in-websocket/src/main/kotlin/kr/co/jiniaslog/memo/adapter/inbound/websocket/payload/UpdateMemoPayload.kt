package kr.co.jiniaslog.memo.adapter.inbound.websocket.payload

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents

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
