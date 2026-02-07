package kr.co.jiniaslog.ai.usecase

interface IDeleteChatSession {
    data class Command(
        val sessionId: Long,
        val authorId: Long,
    )

    operator fun invoke(command: Command)
}
