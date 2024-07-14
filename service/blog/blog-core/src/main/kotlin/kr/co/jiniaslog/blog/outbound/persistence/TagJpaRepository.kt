package kr.co.jiniaslog.blog.outbound.persistence

import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.shared.core.domain.Repository
import org.springframework.data.jpa.repository.JpaRepository

interface TagJpaRepository : JpaRepository<Tag, TagId> {
    fun findByTagName(tagName: TagName): Tag?
}

interface TagRepository : Repository<Tag, TagId> {
    fun findByName(tagName: TagName): Tag?
}

@org.springframework.stereotype.Repository
class TagRepositoryAdapter(
    private val tagJpaRepository: TagJpaRepository,
) : TagRepository {
    override fun save(entity: Tag): Tag {
        return tagJpaRepository.save(entity)
    }

    override fun findByName(tagName: TagName): Tag? {
        return tagJpaRepository.findByTagName(tagName)
    }

    override fun findById(id: TagId): Tag? {
        return tagJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: TagId) {
        tagJpaRepository.deleteById(id)
    }
}
