package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.MemoId

interface IUpdateMemoReferences {
    fun handle(command: Command): Info

    sealed class Command {
        abstract val memoId: MemoId

        data class RemoveReference(
            override val memoId: MemoId,
            val referenceId: MemoId,
        ) : Command()

        // fixme 일괄로 할 이유가 있나? 수정해야겠다
        /**
         * 참조를 업데이트 한다
         *
         * 기존의 값과는 상관없이 post 방식으로 일괄로 업데이트 한다
         *
         */
        data class UpdateReferences(
            override val memoId: MemoId,
            val references: Set<MemoId>,
        ) : Command()
    }

    data class Info(val id: MemoId)
}
