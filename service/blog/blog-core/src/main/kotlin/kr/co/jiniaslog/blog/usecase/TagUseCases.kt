package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName

interface TagUseCasesFacade :
    ICreateNewTag

interface ICreateNewTag {
    fun handle(command: Command): Info

    data class Command(
        val name: TagName,
    )

    data class Info(
        val id: TagId,
    )
}
