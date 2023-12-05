package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.memo.domain.tag.TagId

interface IInitMemo {
    fun handle(command: Command): Info

    data class Command(
        val authorId: AuthorId,
        val title: MemoTitle = MemoTitle(""),
        val content: MemoContent = MemoContent(""),
        val references: Set<MemoId> = mutableSetOf(),
        val tags: Set<TagId> = mutableSetOf(),
    )

    data class Info(val id: MemoId)
}
