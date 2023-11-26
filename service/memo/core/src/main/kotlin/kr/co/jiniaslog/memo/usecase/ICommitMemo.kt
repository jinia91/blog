package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.domain.tag.TagId

interface ICommitMemo {
    fun commit(command: CommitMemoCommand): CommitMemoInfo

    data class CommitMemoCommand(
        val authorId: AuthorId,
        val memoId: MemoId,
        val title: MemoTitle,
        val content: MemoContent,
        val linkedList: List<MemoId>,
        val tags: List<TagId>,
    )

    data class CommitMemoInfo(val id: Long)
}
