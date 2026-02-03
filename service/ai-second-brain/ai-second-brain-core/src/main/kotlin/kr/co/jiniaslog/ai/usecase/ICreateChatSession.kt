package kr.co.jiniaslog.ai.usecase

interface ICreateChatSession {
    data class Command(
        val authorId: Long,
        val title: String? = null,
    )

    data class Info(
        val sessionId: Long,
        val title: String,
    )

    operator fun invoke(command: Command): Info
}
