package kr.co.jiniaslog.memo.adapter.inbound.websocket.payload

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.usecase.IUpdateMemoReferences

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
