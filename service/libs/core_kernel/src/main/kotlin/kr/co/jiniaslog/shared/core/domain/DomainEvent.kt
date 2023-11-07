package kr.co.jiniaslog.shared.core.domain

import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID.randomUUID

abstract class DomainEvent(
    val eventVersion: Int,
    val occurredAt: LocalDateTime,
) : Serializable {
    val id: String = randomUUID().toString()
}
