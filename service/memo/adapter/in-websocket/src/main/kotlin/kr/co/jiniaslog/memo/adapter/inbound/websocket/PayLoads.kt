package kr.co.jiniaslog.memo.adapter.inbound.websocket

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.usecase.ICommitMemo
import kr.co.jiniaslog.memo.usecase.IGetRecommendRelatedMemo
import kr.co.jiniaslog.memo.usecase.IGetRecommendRelatedMemo.GetRecommendRelatedMemoInfo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo.InitMemoCommand

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = InitMemoPayload::class, name = "InitMemo"),
    JsonSubTypes.Type(value = GetRecommendRelatedMemoPayload::class, name = "GetRecommendRelatedMemo"),
    JsonSubTypes.Type(value = CommitMemoPayload::class, name = "CommitMemo"),
)
sealed class PayLoad {
    abstract val type: String
}

data class InitMemoPayload(
    override val type: String = "InitMemo",
    val authorId: Long,
    val content: String,
) : PayLoad() {
    fun toCommand(): InitMemoCommand {
        return InitMemoCommand(
            authorId = AuthorId(authorId),
            content = MemoContent(content),
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

fun IInitMemo.InitMemoInfo.toPayload(): InitMemoResponse {
    return InitMemoResponse.from(id)
}

data class GetRecommendRelatedMemoPayload(
    override val type: String = "GetRecommendRelatedMemo",
    val query: String,
) : PayLoad() {
    fun toCommand(): IGetRecommendRelatedMemo.GetRecommendRelatedMemoCommand {
        return IGetRecommendRelatedMemo.GetRecommendRelatedMemoCommand(
            query = query,
        )
    }
}

data class GetRecommendRelatedMemoResponse(
    val type: String = "GetRecommendRelatedMemoInfo",
    val relatedMemoCandidates: List<Pair<Long, String>>,
)

fun GetRecommendRelatedMemoInfo.toPayload(): GetRecommendRelatedMemoResponse {
    return GetRecommendRelatedMemoResponse(
        relatedMemoCandidates =
            relatedMemoCandidates.map {
                Pair(it.first.value, it.second.value)
            },
    )
}

data class CommitMemoPayload(
    override val type: String = "CommitMemo",
    val id: Long,
    val authorId: Long,
    val content: String,
    val title: String,
    val links: List<Long>?,
) : PayLoad() {
    fun toCommand(): ICommitMemo.CommitMemoCommand {
        return ICommitMemo.CommitMemoCommand(
            authorId = AuthorId(authorId),
            content = MemoContent(content),
            title = MemoTitle(title),
            tags = listOf(),
            linkedList = links?.map { MemoId(it) } ?: listOf(),
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

fun ICommitMemo.CommitMemoInfo.toPayload(): CommitMemoResponse {
    return CommitMemoResponse.from(id)
}
