package kr.co.jiniaslog.blog.adapter.outbound.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.QArticle.article
import kr.co.jiniaslog.blog.domain.article.Tagging
import kr.co.jiniaslog.blog.domain.article.TaggingId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.queries.IGetSimpleArticleListWithCursor
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

    override fun getSimpleArticleListWithCursor(
        cursor: ArticleId,
        limit: Int,
        published: Boolean,
    ): List<IGetSimpleArticleListWithCursor.Info> {
        return blogJpaQueryFactory
            .selectFrom(article)
            .where(article.entityId.value.gt(cursor.value))
            .apply {
                if (published) {
                    this.where(article.status.eq(Article.Status.PUBLISHED))
                } else {
                    this.where(article.status.eq(Article.Status.DRAFT))
                }
            }
            .limit(limit.toLong())
            .fetch()
            .map {
                IGetSimpleArticleListWithCursor.Info(
                    id = it.id.value,
                    title = if (published) it.articleContents.title else it.draftContents.title,
                    thumbnailUrl = if (published) it.articleContents.thumbnailUrl else it.draftContents.thumbnailUrl,
                    content = (if (published) it.articleContents.contents else it.draftContents.contents).take(100),
                    createdAt = it.createdAt!!,
                    tags = it.tagsInfo.mapKeys { it.key.id },
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
