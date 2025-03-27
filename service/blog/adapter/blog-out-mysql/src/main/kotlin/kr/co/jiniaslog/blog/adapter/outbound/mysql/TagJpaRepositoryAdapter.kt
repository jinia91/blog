package kr.co.jiniaslog.blog.adapter.outbound.mysql

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.blog.domain.article.QArticle.article
import kr.co.jiniaslog.blog.domain.article.QTagging.tagging
import kr.co.jiniaslog.blog.domain.tag.QTag.tag
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.TagRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface TagJpaRepository : JpaRepository<Tag, TagId> {
    fun findByTagName(tagName: TagName): Tag?
}

@Repository
class TagRepositoryAdapter(
    private val tagJpaRepository: TagJpaRepository,
    private val blogJpaQueryFactory: JPAQueryFactory,
) : TagRepository {
    override fun save(entity: Tag): Tag {
        return tagJpaRepository.save(entity)
    }

    override fun findByName(tagName: TagName): Tag? {
        return tagJpaRepository.findByTagName(tagName)
    }

    override fun findUnUsedTags(): List<Tag> {
        return blogJpaQueryFactory
            .selectFrom(tag)
            .where(
                tag.entityId.notIn(
                    JPAExpressions.select(tagging.tag.entityId)
                        .from(article)
                        .join(article._tags, tagging)
                        .where(tagging.tag.isNotNull)
                )
            ).fetch()
    }

    override fun findTopNTags(n: Int): List<Tag> {
        return blogJpaQueryFactory
            .select(tag)
            .from(tagging)
            .join(tagging.tag, tag)
            .groupBy(tag)
            .orderBy(tagging.count().desc())
            .limit(n.toLong())
            .fetch()
    }

    override fun findById(id: TagId): Tag? {
        return tagJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: TagId) {
        tagJpaRepository.deleteById(id)
    }
}
