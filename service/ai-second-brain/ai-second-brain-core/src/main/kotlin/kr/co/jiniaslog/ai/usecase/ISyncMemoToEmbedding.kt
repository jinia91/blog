package kr.co.jiniaslog.ai.usecase

interface ISyncMemoToEmbedding {
    data class Command(
        val memoId: Long,
        val authorId: Long,
        val title: String,
        val content: String,
    )

    operator fun invoke(command: Command)
}
