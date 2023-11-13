package kr.co.jiniaslog.shared.adapter.out.rdb

import org.springframework.data.domain.Persistable
import java.time.LocalDateTime

abstract class AbstractPM : Persistable<Long> {
    abstract val id: Long
    abstract var createdAt: LocalDateTime?
    abstract var updatedAt: LocalDateTime?

    override fun getId(): Long {
        return id
    }

    override fun isNew(): Boolean = createdAt == null
}
