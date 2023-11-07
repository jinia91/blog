package kr.co.jiniaslog.shared.core.domain

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

abstract class DomainEvent(
    val eventVersion: Int,
    val occurredAt: LocalDateTime
    ) : Serializable {
    val id: String = UUID.randomUUID().toString()
}