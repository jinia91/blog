package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent

interface IInitMemo {
    fun init(command: InitMemoCommand): InitMemoInfo

    data class InitMemoCommand(
        val authorId: AuthorId,
        val content: MemoContent,
    )

    data class InitMemoInfo(val id: Long)
}
