package kr.co.jiniaslog.ai.adapter.outbound.memo

import kr.co.jiniaslog.ai.outbound.MemoCommandService
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.domain.memo.MemoContent
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.memo.domain.memo.MemoTitle
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
class MemoCommandServiceAdapter(
    private val memoRepository: MemoRepository,
) : MemoCommandService {

    override fun createMemo(authorId: Long, title: String, content: String): Long {
        val memo = Memo.init(
            authorId = AuthorId(authorId),
            parentFolderId = null,
        ).apply {
            update(
                title = MemoTitle(title),
                content = MemoContent(content),
            )
        }

        val saved = memoRepository.save(memo)
        return saved.entityId.value
    }
}
