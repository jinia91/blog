package kr.co.jiniaslog.shared.core.domain

import java.io.Serializable
import java.time.LocalDateTime

abstract class Message(val isRecovery: Boolean)

abstract class DomainEvent(
    val eventVersion: Int,
    val occurredOn: LocalDateTime,
    isRecovery: Boolean,
) : Message(isRecovery), Serializable
