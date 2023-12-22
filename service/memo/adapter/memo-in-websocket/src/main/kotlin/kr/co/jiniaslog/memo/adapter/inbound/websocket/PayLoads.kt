package kr.co.jiniaslog.memo.adapter.inbound.websocket

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.NotEmpty
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.usecase.IUpdateMemo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = UpdateMemoPayload::class, name = "UpdateMemo"),
    JsonSubTypes.Type(value = AddReferencePayload::class, name = "AddReference"),
    JsonSubTypes.Type(value = RemoveReferencePayload::class, name = "RemoveReference"),
)
sealed class PayLoad {
    abstract val type: String
}

data class RecommendRelatedMemoResponse(
    val type: String = "GetRecommendRelatedMemoInfo",
    val relatedMemoCandidates: List<Pair<Long, String>>,
)

fun IRecommendRelatedMemo.Info.toResponse(): RecommendRelatedMemoResponse {
    return RecommendRelatedMemoResponse(
        relatedMemoCandidates =
            relatedMemoCandidates.map {
                Pair(it.first.value, it.second.value)
            },
    )
}

data class CommitMemoResponse(
    val id: Long,
) {
    companion object {
        fun from(id: Long): CommitMemoResponse {
            return CommitMemoResponse(
                id = id,
            )
        }
    }
}

data class UpdateMemoPayload(
    override val type: String = "UpdateMemo",
    val id: Long,
    @field:NotEmpty
    val content: String,
    @field:NotEmpty
    val title: String,
) : PayLoad() {
    fun toCommand(): IUpdateMemo.Command.UpdateForm {
        return IUpdateMemo.Command.UpdateForm(
            content = MemoContent(content),
            title = MemoTitle(title),
            memoId = MemoId(id),
        )
    }
}

data class UpdateMemoResponse(
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

fun IUpdateMemo.Info.toResponse(): UpdateMemoResponse {
    return UpdateMemoResponse.from(id.value)
}

data class AddReferencePayload(
    override val type: String = "AddReference",
    val id: Long,
    val referenceId: Long,
) : PayLoad() {
    fun toCommand(): IUpdateMemo.Command.AddReference {
        return IUpdateMemo.Command.AddReference(
            memoId = MemoId(id),
            referenceId = MemoId(referenceId),
        )
    }
}

data class RemoveReferencePayload(
    override val type: String = "RemoveReference",
    val id: Long,
    val referenceId: Long,
) : PayLoad() {
    fun toCommand(): IUpdateMemo.Command.RemoveReference {
        return IUpdateMemo.Command.RemoveReference(
            memoId = MemoId(id),
            referenceId = MemoId(referenceId),
        )
    }
}