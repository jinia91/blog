package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.shared.core.domain.DomainEntity
import java.time.LocalDateTime

class Tagging(
    id: MemoTagId,
    memoId: MemoId,
    tagId: TagId,
) : DomainEntity<MemoTagId>() {
    override val id: MemoTagId = id
    var tagId: TagId = tagId
        private set
    var memoId: MemoId = memoId
        private set

    companion object {
        fun from(
            id: MemoTagId,
            tagId: TagId,
            memoId: MemoId,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Tagging {
            return Tagging(id = id, tagId = tagId, memoId = memoId).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
