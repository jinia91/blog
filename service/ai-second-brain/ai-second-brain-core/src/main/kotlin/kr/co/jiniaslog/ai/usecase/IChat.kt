package kr.co.jiniaslog.ai.usecase

interface IChat {
    data class Command(
        val sessionId: Long,
        val authorId: Long,
        val message: String,
    )

    data class Info(
        val sessionId: Long,
        val response: String,
        val createdMemoId: Long?,
    )

    operator fun invoke(command: Command): Info
}
