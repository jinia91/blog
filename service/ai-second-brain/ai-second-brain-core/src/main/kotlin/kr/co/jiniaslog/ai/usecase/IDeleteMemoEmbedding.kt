package kr.co.jiniaslog.ai.usecase

interface IDeleteMemoEmbedding {
    data class Command(
        val memoId: Long,
    )

    operator fun invoke(command: Command)
}
