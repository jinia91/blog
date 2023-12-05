package kr.co.jiniaslog.memo.adapter.inbound.websocket

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.usecase.ICommitMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IUpdateMemo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = InitMemoPayload::class, name = "InitMemo"),
    JsonSubTypes.Type(value = CommitMemoPayload::class, name = "CommitMemo"),
    JsonSubTypes.Type(value = UpdateMemoPayload::class, name = "UpdateMemo"),
    JsonSubTypes.Type(value = AddReferencePayload::class, name = "AddReference"),
    JsonSubTypes.Type(value = RemoveReferencePayload::class, name = "RemoveReference"),
    JsonSubTypes.Type(value = AddTagPayload::class, name = "AddTag"),
    JsonSubTypes.Type(value = RemoveTagPayload::class, name = "RemoveTag"),
    JsonSubTypes.Type(value = RecommendRelatedMemoPayload::class, name = "GetRecommendRelatedMemo"),
)
sealed class PayLoad {
    abstract val type: String
}

data class InitMemoPayload(
    override val type: String = "InitMemo",
    val authorId: Long,
    val title: String?,
    val content: String?,
    val references: Set<Long>?,
    val tags: Set<Long>?,
) : PayLoad() {
    fun toCommand(): IInitMemo.Command {
        return IInitMemo.Command(
            authorId = AuthorId(authorId),
            title = title?.let { MemoTitle(title) } ?: MemoTitle(""),
            content = content?.let { MemoContent(content) } ?: MemoContent(""),
            references = references?.map { MemoId(it) }?.toMutableSet() ?: mutableSetOf(),
            tags = tags?.map { TagId(it) }?.toMutableSet() ?: mutableSetOf(),
        )
    }
}

data class InitMemoResponse(
    val type: String,
    val id: Long,
) {
    companion object {
        fun from(id: Long): InitMemoResponse {
            return InitMemoResponse(
                type = "InitMemoInfo",
                id = id,
            )
        }
    }
}

fun IInitMemo.Info.toResponse(): InitMemoResponse {
    return InitMemoResponse.from(id.value)
}

data class RecommendRelatedMemoPayload(
    override val type: String = "GetRecommendRelatedMemo",
    val query: String,
) : PayLoad() {
    fun toQuery(): IRecommendRelatedMemo.Query {
        return IRecommendRelatedMemo.Query(
            query = query,
        )
    }
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

data class CommitMemoPayload(
    override val type: String = "CommitMemo",
    val id: Long,
    val content: String,
    val title: String,
) : PayLoad() {
    fun toCommand(): ICommitMemo.Command {
        return ICommitMemo.Command(
            content = MemoContent(content),
            title = MemoTitle(title),
            memoId = MemoId(id),
        )
    }
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

fun ICommitMemo.Info.toResponse(): CommitMemoResponse {
    return CommitMemoResponse.from(id.value)
}

data class UpdateMemoPayload(
    override val type: String = "UpdateMemo",
    val id: Long,
    val content: String,
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

data class AddTagPayload(
    override val type: String = "AddTag",
    val id: Long,
    val tagId: Long,
) : PayLoad() {
    fun toCommand(): IUpdateMemo.Command.AddTag {
        return IUpdateMemo.Command.AddTag(
            memoId = MemoId(id),
            tagId = TagId(tagId),
        )
    }
}

data class RemoveTagPayload(
    override val type: String = "RemoveTag",
    val id: Long,
    val tagId: Long,
) : PayLoad() {
    fun toCommand(): IUpdateMemo.Command.RemoveTag {
        return IUpdateMemo.Command.RemoveTag(
            memoId = MemoId(id),
            tagId = TagId(tagId),
        )
    }
}
