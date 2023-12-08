package kr.co.jiniaslog.memo.adapter.inbound.websocket

import kr.co.jiniaslog.memo.queries.IRecommendRelatedMemo
import kr.co.jiniaslog.memo.queries.impl.MemoQueriesFacade
import kr.co.jiniaslog.memo.usecase.ICommitMemo
import kr.co.jiniaslog.memo.usecase.IInitMemo
import kr.co.jiniaslog.memo.usecase.IUpdateMemo
import kr.co.jiniaslog.memo.usecase.impl.MemoUseCasesFacade
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

private const val MEMO_PROTOCOL = "/topic/memoResponse"
private val log = mu.KotlinLogging.logger { }

@Controller
class MemoWebSocketHandler(
    private val memoUseCases: MemoUseCasesFacade,
    private val memoQueries: MemoQueriesFacade,
) {
    @MessageMapping("/initMemo")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: InitMemoPayload): InitMemoResponse {
        log.debug { "payload: $payload" }
        val command: IInitMemo.Command = payload.toCommand()
        return memoUseCases.handle(command)
            .toResponse()
    }

    @MessageMapping("/updateMemo")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: UpdateMemoPayload): UpdateMemoResponse {
        val command: IUpdateMemo.Command = payload.toCommand()
        return memoUseCases.handle(command)
            .toResponse()
    }

    @MessageMapping("/addReference")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: AddReferencePayload): UpdateMemoResponse {
        val command: IUpdateMemo.Command.AddReference = payload.toCommand()
        return memoUseCases.handle(command)
            .toResponse()
    }

    @MessageMapping("/removeReference")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: RemoveReferencePayload): UpdateMemoResponse {
        val command: IUpdateMemo.Command.RemoveReference = payload.toCommand()
        return memoUseCases.handle(command)
            .toResponse()
    }

    @MessageMapping("/addTag")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: AddTagPayload): UpdateMemoResponse {
        val command: IUpdateMemo.Command.AddTag = payload.toCommand()
        return memoUseCases.handle(command)
            .toResponse()
    }

    @MessageMapping("/removeTag")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: RemoveTagPayload): UpdateMemoResponse {
        val command: IUpdateMemo.Command.RemoveTag = payload.toCommand()
        return memoUseCases.handle(command)
            .toResponse()
    }

    @MessageMapping("/commitMemo")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: CommitMemoPayload): CommitMemoResponse {
        val command: ICommitMemo.Command = payload.toCommand()
        return memoUseCases.handle(command)
            .toResponse()
    }

    @MessageMapping("/getRecommendRelatedMemo")
    @SendTo(MEMO_PROTOCOL)
    fun handle(payload: RecommendRelatedMemoPayload): RecommendRelatedMemoResponse {
        val query: IRecommendRelatedMemo.Query = payload.toQuery()
        return memoQueries.handle(query)
            .toResponse()
    }
}
