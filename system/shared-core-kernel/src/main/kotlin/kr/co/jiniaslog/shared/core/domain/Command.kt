package kr.co.jiniaslog.shared.core.domain

abstract class Command(
    isRecovery: Boolean,
) : Message(isRecovery)
