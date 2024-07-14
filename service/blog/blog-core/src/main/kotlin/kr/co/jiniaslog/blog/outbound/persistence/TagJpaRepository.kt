package kr.co.jiniaslog.blog.outbound.persistence

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import kr.co.jiniaslog.blog.domain.article.QArticle.article
import kr.co.jiniaslog.blog.domain.article.QTagging.tagging
import kr.co.jiniaslog.blog.domain.tag.QTag.tag
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.shared.core.domain.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

interface TagJpaRepository : JpaRepository<Tag, TagId> {
    fun findByTagName(tagName: TagName): Tag?
}

interface TagRepository : Repository<Tag, TagId> {
    fun findByName(tagName: TagName): Tag?

    fun findUnUsedTags(): List<Tag>
}

@org.springframework.stereotype.Repository
class TagRepositoryAdapter(
    private val tagJpaRepository: TagJpaRepository,
    private val queryFactory: JPAQueryFactory,
    private val em: EntityManager
) : TagRepository {
    override fun save(entity: Tag): Tag {
        return tagJpaRepository.save(entity)
    }

    override fun findByName(tagName: TagName): Tag? {
        return tagJpaRepository.findByTagName(tagName)
    }

    override fun findUnUsedTags(): List<Tag> {
        return queryFactory
            .selectFrom(tag)
            .where(
                tag.entityId.notIn(
                    JPAExpressions.select(tagging.tagId)
                        .from(article)
                        .join(article._tags, tagging)
                        .where(tagging.tagId.isNotNull)
                )
            ).fetch()
    }

    override fun findById(id: TagId): Tag? {
        return tagJpaRepository.findById(id).orElse(null)
    }

    // fixme 대체 왜 안됨?
    @Transactional("blogTransactionManager", isolation = Isolation.REPEATABLE_READ)
    override fun deleteById(id: TagId) {
        queryFactory.delete(tag)
            .where(tag.entityId.eq(id))
            .execute()
//        tagJpaRepository.deleteById(id)
    }
}
