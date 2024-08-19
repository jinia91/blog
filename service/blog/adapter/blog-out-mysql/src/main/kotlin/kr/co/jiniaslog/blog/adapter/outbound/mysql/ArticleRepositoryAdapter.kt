package kr.co.jiniaslog.blog.adapter.outbound.mysql

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleJpaRepository : JpaRepository<Article, ArticleId>

@org.springframework.stereotype.Repository
class ArticleRepositoryAdapter(
    private val articleJpaRepository: ArticleJpaRepository,
) : ArticleRepository {
    override fun save(entity: Article): Article {
        return articleJpaRepository.save(entity)
    }

    override fun findById(id: ArticleId): Article? {
        return articleJpaRepository.findById(id).orElse(null)
    }

    override fun deleteById(id: ArticleId) {
        articleJpaRepository.deleteById(id)
    }
}
