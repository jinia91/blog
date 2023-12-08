package kr.co.jiniaslog.memo.usecase

import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName

interface ICreateNewTag {
    fun handle(command: Command): Info

    data class Command(
        val name: TagName,
    )

    data class Info(
        val tagId: TagId,
    )
}
