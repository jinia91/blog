package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.DomainEntity
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

class MemoLink(
    linkId: MemoLinkId,
    rootId: MemoId,
    linkType: MemoLinkType,
    linkedMemoId: MemoId,
) : DomainEntity<MemoLinkId>() {
    override val id: MemoLinkId = linkId

    var rootId: MemoId = rootId
        private set

    var linkType: MemoLinkType = linkType
        private set

    var linkedMemoId: MemoId = linkedMemoId

    companion object {
        fun create(
            rootId: MemoId,
            linkType: MemoLinkType,
            linkedMemoId: MemoId,
        ): MemoLink {
            return MemoLink(
                linkId = MemoLinkId(IdUtils.generate()),
                rootId = rootId,
                linkType = linkType,
                linkedMemoId = linkedMemoId,
            )
        }

        fun from(
            id: MemoLinkId,
            rootId: MemoId,
            linkType: MemoLinkType,
            linkedMemoId: MemoId,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): MemoLink {
            return MemoLink(
                linkId = id,
                rootId = rootId,
                linkType = linkType,
                linkedMemoId = linkedMemoId,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
