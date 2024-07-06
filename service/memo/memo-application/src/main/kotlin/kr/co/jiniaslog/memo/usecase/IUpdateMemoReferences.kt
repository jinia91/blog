package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.MemoId

interface IUpdateMemoReferences {
    fun handle(command: Command): Info

    sealed class Command {
        abstract val memoId: MemoId

        data class AddReference(
            override val memoId: MemoId,
            val referenceId: MemoId,
        ) : Command()

        data class RemoveReference(
            override val memoId: MemoId,
            val referenceId: MemoId,
        ) : Command()

        data class UpdateReferences(
            override val memoId: MemoId,
            val references: Set<MemoId>,
        ) : Command()
    }

    data class Info(val id: MemoId)
}
