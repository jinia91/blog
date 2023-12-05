package kr.co.jiniaslog.fakes

import kr.co.jiniaslog.memo.domain.tag.Tag
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import java.time.LocalDateTime

class FakeTagRepository : TagRepository {
    private val tags = mutableListOf<Tag>()

    override fun findByName(name: TagName): Tag? {
        return tags.find { it.name == name }
    }

    override fun findById(id: TagId): Tag? {
        return tags.find { it.id == id }
    }

    override fun findAll(): List<Tag> {
        return tags
    }

    override fun deleteById(id: TagId) {
        tags.removeIf { it.id == id }
    }

    override fun save(entity: Tag): Tag {
        tags.add(entity)
        entity.apply {
            createdAt = createdAt ?: LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
        return entity
    }
}
