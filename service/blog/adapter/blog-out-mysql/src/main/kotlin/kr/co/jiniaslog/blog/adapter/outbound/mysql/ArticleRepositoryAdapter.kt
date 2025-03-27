package kr.co.jiniaslog.blog.adapter.outbound.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleVo
import kr.co.jiniaslog.blog.domain.article.QArticle.article
import kr.co.jiniaslog.blog.domain.article.Tagging
import kr.co.jiniaslog.blog.domain.article.TaggingId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface ArticleJpaRepository : JpaRepository<Article, ArticleId>

interface TaggingJpaRepository : JpaRepository<Tagging, TaggingId>

@Repository
class ArticleRepositoryAdapter(
    private val articleJpaRepository: ArticleJpaRepository,
    private val blogJpaQueryFactory: JPAQueryFactory,
) : ArticleRepository {
    override fun save(entity: Article): Article {
        return articleJpaRepository.save(entity)
    }

    override fun getArticleListWithCursor(
        cursor: Long,
        limit: Int,
        published: Boolean,
    ): List<ArticleVo> {
        return blogJpaQueryFactory
            .selectFrom(article)
            .where(article.entityId.value.lt(cursor))
            .apply {
                if (published) {
                    this.where(article.status.eq(Article.Status.PUBLISHED))
                } else {
                    this.where(article.status.eq(Article.Status.DRAFT))
                }
            }
            .limit(limit.toLong())
            .orderBy(article.entityId.value.desc())
            .fetch()
            .map {
                val articleContentByPublished = if (published) it.articleContents else it.draftContents
                ArticleVo(
                    id = it.id.value,
                    title = articleContentByPublished.title,
                    thumbnailUrl = articleContentByPublished.thumbnailUrl,
                    content = articleContentByPublished.contents,
                    createdAt = it.createdAt!!,
                    tags = it.tagsInfo.mapKeys { it.key.id },
                    status = it.status.name,
                    voDataStatus = if (published) Article.Status.PUBLISHED else Article.Status.DRAFT
                )
            }
    }

    override fun findById(id: ArticleId): Article? {
        return articleJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: ArticleId) {
        articleJpaRepository.deleteById(id)
    }
}
