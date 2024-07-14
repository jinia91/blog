package kr.co.jiniaslog.blog.outbound.persistence

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.QArticle.article
import kr.co.jiniaslog.shared.core.domain.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

interface ArticleRepository : Repository<Article, ArticleId>

interface ArticleJpaRepository : JpaRepository<Article, ArticleId>

@org.springframework.stereotype.Repository
class ArticleRepositoryAdapter(
    private val articleJpaRepository: ArticleJpaRepository,
    private val jpaQueryFactory: JPAQueryFactory
) : ArticleRepository {
    override fun save(entity: Article): Article {
        return articleJpaRepository.save(entity)
    }

    override fun findById(id: ArticleId): Article? {
        return articleJpaRepository.findById(id).orElse(null)
    }

    @Transactional("blogTransactionManager", isolation = Isolation.REPEATABLE_READ)
    override fun deleteById(id: ArticleId) {
        jpaQueryFactory
            .delete(article)
            .where(article.entityId.eq(id))
            .execute()
    }
}
