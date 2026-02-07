package kr.co.jiniaslog.ai.usecase

interface ISyncAllMemosToEmbedding {
    data class Command(
        val authorId: Long,
    )

    data class Info(
        val syncedCount: Int,
    )

    operator fun invoke(command: Command): Info
}
