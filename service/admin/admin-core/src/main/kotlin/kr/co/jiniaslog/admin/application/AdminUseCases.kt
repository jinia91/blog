package kr.co.jiniaslog.admin.application

interface AdminUseCases :
    CreateMockUser

interface CreateMockUser {
    fun handle(command: Command): Info

    class Command()

    class Info()
}
