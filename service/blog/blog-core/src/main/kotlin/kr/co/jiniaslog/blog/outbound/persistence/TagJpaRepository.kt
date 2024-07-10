package kr.co.jiniaslog.blog.outbound.persistence

import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.shared.core.domain.Repository
import org.springframework.data.jpa.repository.JpaRepository

interface TagJpaRepository : JpaRepository<Tag, TagId>

interface TagRepository : Repository<Tag, TagId>

@org.springframework.stereotype.Repository
class TagRepositoryAdapter(
    private val tagJpaRepository: TagJpaRepository,
) : TagRepository {
    override fun save(entity: Tag): Tag {
        return tagJpaRepository.save(entity)
    }

    override fun findById(id: TagId): Tag? {
        return tagJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: TagId) {
        tagJpaRepository.deleteById(id)
    }
}
