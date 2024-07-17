package kr.co.jiniaslog.blog.domain.tag.dto

import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName

data class TagDto(
    val tagId: TagId,
    val tagName: TagName
)
