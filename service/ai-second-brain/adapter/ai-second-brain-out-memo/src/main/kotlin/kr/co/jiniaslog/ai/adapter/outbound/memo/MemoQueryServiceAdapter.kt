package kr.co.jiniaslog.ai.adapter.outbound.memo

import kr.co.jiniaslog.ai.outbound.MemoInfo
import kr.co.jiniaslog.ai.outbound.MemoQueryService
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.MemoId
import kr.co.jiniaslog.memo.domain.memo.MemoRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter

@PersistenceAdapter
class MemoQueryServiceAdapter(
    private val memoRepository: MemoRepository,
) : MemoQueryService {

    override fun getMemoById(memoId: Long): MemoInfo? {
        return memoRepository.findById(MemoId(memoId))?.let { memo ->
            MemoInfo(
                id = memo.entityId.value,
                authorId = memo.authorId.value,
                title = memo.title.value,
                content = memo.content.value,
            )
        }
    }

    override fun getAllMemosByAuthorId(authorId: Long): List<MemoInfo> {
        return memoRepository.findAllByAuthorId(AuthorId(authorId)).map { memo ->
            MemoInfo(
                id = memo.entityId.value,
                authorId = memo.authorId.value,
                title = memo.title.value,
                content = memo.content.value,
            )
        }
    }
}
