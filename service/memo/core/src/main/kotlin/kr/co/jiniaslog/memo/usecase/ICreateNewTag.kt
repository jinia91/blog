package kr.co.jiniaslog.memo.usecase

interface ICreateNewTag {
    fun handle(command: Command): Info

    data class Command(
        val name: String,
    )

    data class Info(
        val tagId: Long,
    )
}
