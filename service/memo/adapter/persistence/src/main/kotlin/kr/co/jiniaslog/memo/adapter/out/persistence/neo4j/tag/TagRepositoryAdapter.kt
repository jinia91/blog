package kr.co.jiniaslog.memo.adapter.out.persistence.neo4j.tag

import kr.co.jiniaslog.memo.domain.tag.Tag
import kr.co.jiniaslog.memo.domain.tag.TagId
import kr.co.jiniaslog.memo.domain.tag.TagName
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter
import kotlin.jvm.optionals.getOrNull

@PersistenceAdapter
class TagRepositoryAdapter(
    private val memoNeo4jRepository: TagNeo4jRepository,
) : TagRepository {
    override fun findByName(name: TagName): Tag? {
        return memoNeo4jRepository.findByName(name.value)?.toDomain()
    }

    override fun findById(id: TagId): Tag? {
        return memoNeo4jRepository.findById(id.value).getOrNull()?.toDomain()
    }

    override fun findAll(): List<Tag> {
        return memoNeo4jRepository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: TagId) {
        memoNeo4jRepository.deleteById(id.value)
    }

    override fun save(entity: Tag): Tag {
        val tagNeo4jEntity =
            TagNeo4jEntity(
                id = entity.id.value,
                name = entity.name.value,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
            )

        return memoNeo4jRepository.save(tagNeo4jEntity).toDomain()
    }
}
