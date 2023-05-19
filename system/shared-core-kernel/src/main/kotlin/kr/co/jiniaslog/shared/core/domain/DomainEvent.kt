package kr.co.jiniaslog.shared.core.domain

import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

abstract class Message(val isRecovery: Boolean)

abstract class DomainEvent(
    val eventVersion: Int,
    isRecovery: Boolean,
) : Message(isRecovery), Serializable {
    abstract val occurredAt: LocalDateTime
    val id: String = UUID.randomUUID().toString()

    companion object {
        const val EVENT_ID = "eventID"
    }
}
