package kr.co.jiniaslog.blog.usecase.tag

import kr.co.jiniaslog.blog.domain.tag.dto.TagDto

interface IDeleteUnUsedTags {
    fun handle(command: Command): Info

    class Command()

    data class Info(
        val deletedTags: List<TagDto>
    )
}
