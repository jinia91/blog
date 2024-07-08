package kr.co.jiniaslog.blog.outbound.persistence

import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName
import org.springframework.data.jpa.repository.JpaRepository

interface TagJpaRepository : JpaRepository<Tag, TagId> {
    fun findByTagName(name: TagName): Tag?
}
