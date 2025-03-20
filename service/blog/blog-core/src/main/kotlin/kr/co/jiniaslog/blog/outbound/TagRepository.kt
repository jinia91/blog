package kr.co.jiniaslog.blog.outbound

import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.shared.core.domain.Repository

interface TagRepository : Repository<Tag, TagId> {
    fun findByName(tagName: TagName): Tag?

    fun findUnUsedTags(): List<Tag>
}
