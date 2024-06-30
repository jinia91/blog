package kr.co.jiniaslog.memo.domain

import kr.co.jiniaslog.memo.domain.folder.FolderId
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoReference
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

object MemoTestFixtures {
    fun build(
        id: MemoId = MemoId(IdUtils.generate()),
        authorId: AuthorId = AuthorId(IdUtils.generate()),
        content: MemoContent = MemoContent("test content"),
        title: MemoTitle = MemoTitle("test title"),
        references: MutableSet<MemoReference> = mutableSetOf(),
        parentFolderId: FolderId? = null,
        createdAt: LocalDateTime? = null,
        updatedAt: LocalDateTime? = null,
    ): Memo {
        return Memo.from(
            id = id,
            authorId = authorId,
            content = content,
            title = title,
            reference = references,
            parentFolderId = parentFolderId,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
