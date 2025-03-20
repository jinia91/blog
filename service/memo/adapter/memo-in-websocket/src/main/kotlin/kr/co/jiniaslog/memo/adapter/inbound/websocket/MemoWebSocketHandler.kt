package kr.co.jiniaslog.memo.adapter.inbound.websocket

import jakarta.validation.Valid
import kr.co.jiniaslog.memo.adapter.inbound.websocket.payload.UpdateMemoPayload
import kr.co.jiniaslog.memo.adapter.inbound.websocket.payload.UpdateMemoResponse
import kr.co.jiniaslog.memo.adapter.inbound.websocket.payload.UpdateReferencesPayload
import kr.co.jiniaslog.memo.adapter.inbound.websocket.payload.UpdateReferencesResponse
import kr.co.jiniaslog.memo.adapter.inbound.websocket.payload.toResponse
import kr.co.jiniaslog.memo.usecase.IUpdateMemoContents
import kr.co.jiniaslog.memo.usecase.IUpdateMemoReferences
import kr.co.jiniaslog.memo.usecase.MemoUseCasesFacade
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

private const val MEMO_PROTOCOL = "/topic/memoResponse"

@Controller
class MemoWebSocketHandler(
    private val memoUseCases: MemoUseCasesFacade,
) {
    @MessageMapping("memo/updateMemo")
    @SendTo(MEMO_PROTOCOL)
    fun handle(
        @Valid payload: UpdateMemoPayload,
    ): UpdateMemoResponse {
        val command: IUpdateMemoContents.Command = payload.toCommand()
        val info = memoUseCases.handle(command)
        return info.toResponse()
    }

    @MessageMapping("memo/updateReferences")
    @SendTo("$MEMO_PROTOCOL/updateReferences")
    fun handle(
        @Valid payload: UpdateReferencesPayload,
    ): UpdateReferencesResponse {
        val command: IUpdateMemoReferences.Command = payload.toCommand()
        val info = memoUseCases.handle(command)
        return info.toResponse()
    }
}
