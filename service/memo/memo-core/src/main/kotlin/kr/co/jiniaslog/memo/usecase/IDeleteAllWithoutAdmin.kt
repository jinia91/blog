package kr.co.jiniaslog.memo.usecase

interface IDeleteAllWithoutAdmin {
    fun handle(command: Command): Info
    class Command()
    class Info()
}
