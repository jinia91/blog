package kr.co.jiniaslog.blog.usecase.tag

import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName

interface ICreateNewTagIfNotExist {
    fun handle(command: Command): Info

    data class Command(
        val name: TagName
    )

    data class Info(
        val tagId: TagId
    )
}
