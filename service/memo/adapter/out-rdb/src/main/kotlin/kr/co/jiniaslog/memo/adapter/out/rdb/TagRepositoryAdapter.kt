package kr.co.jiniaslog.memo.adapter.out.rdb

import kr.co.jiniaslog.memo.domain.tag.Tag
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kr.co.jiniaslog.shared.core.domain.IdGenerator
import kotlin.jvm.optionals.getOrNull

@PersistenceAdapter
class TagRepositoryAdapter(
    private val tagPMRepository: TagPMRepository,
    private val idGenerator: IdGenerator,
) : TagRepository {
    override fun findByName(name: TagName): Tag? {
        return tagPMRepository.findByName(name.value)?.toDomain()
    }

    override fun nextId(): TagId {
        return TagId(idGenerator.generate())
    }

    override fun findById(id: TagId): Tag? {
        return tagPMRepository.findById(id.value).getOrNull()?.toDomain()
    }

    override fun findAll(): List<Tag> {
        return tagPMRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: TagId) {
        tagPMRepository.deleteById(id.value)
    }

    override fun save(entity: Tag): Tag {
        return tagPMRepository.save(entity.toPm()).toDomain()
    }
}
