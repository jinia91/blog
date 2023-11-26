package kr.co.jiniaslog.memo.domain.memo

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import java.time.LocalDateTime

class Memo private constructor(
    id: MemoId,
    content: MemoContent,
    title: MemoTitle,
    tags: List<Tagging>,
    links: List<MemoLink>,
    status: MemoStatus,
) : AggregateRoot<MemoId>() {
    override val id: MemoId = id

    var title: MemoTitle = title
        private set

    var content: MemoContent = content
        private set

    var links: List<MemoLink> = links
        private set

    var tags: List<Tagging> = tags
        private set

    var status = status
        private set

    fun commit(
        title: MemoTitle,
        content: MemoContent,
        linkedList: List<MemoId>,
//        tags: List<Tagging>,
    ) {
        this.title = title
        this.content = content
        this.links = linkedList.map { MemoLink.create(this.id, MemoLinkType.REFERENCE, it) }
//        this.tags = tags
        this.status = MemoStatus.COMMITTED
    }

    companion object {
        fun init(
            id: MemoId,
            title: MemoTitle? = null,
            content: MemoContent,
        ): Memo {
            return Memo(
                id = id,
                content = content,
                title = title ?: MemoTitle.from(content),
                tags = emptyList(),
                links = emptyList(),
                status = MemoStatus.STAGED,
            )
        }

        fun from(
            id: MemoId,
            title: MemoTitle?,
            content: MemoContent,
            tags: List<Tagging>,
            status: MemoStatus,
            links: List<MemoLink>,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Memo {
            return Memo(
                id = id,
                content = content,
                title = title ?: MemoTitle.from(content),
                tags = tags,
                status = status,
                links = links,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
