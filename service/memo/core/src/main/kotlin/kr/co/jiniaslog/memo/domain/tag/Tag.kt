package kr.co.jiniaslog.memo.domain.tag

import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.time.LocalDateTime

class Tag private constructor(
    id: TagId,
    name: TagName,
) : AggregateRoot<TagId>() {
    override val id: TagId = id
    var name: TagName = name
        private set

    companion object {
        fun init(name: TagName): Tag {
            return Tag(
                id = TagId(IdUtils.generate()),
                name = name,
            )
        }

        fun from(
            id: TagId,
            name: TagName,
            createdAt: LocalDateTime?,
            updatedAt: LocalDateTime?,
        ): Tag {
            return Tag(
                id = id,
                name = name,
            ).apply {
                this.createdAt = createdAt
                this.updatedAt = updatedAt
            }
        }
    }
}
