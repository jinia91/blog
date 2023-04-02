package kr.co.jiniaslog.article.adapter.persistence.article

import kr.co.jiniaslog.article.application.port.ArticleIdGenerator
import kr.co.jiniaslog.article.application.port.ArticleRepository
import kr.co.jiniaslog.article.domain.Article
import kr.co.jiniaslog.article.domain.ArticleId
import kr.co.jiniaslog.shared.persistence.id.IdGenerator
import org.springframework.stereotype.Repository

@Repository
class ArticleRepositoryAdapter(
    private val jpaArticleRepository: JpaArticleRepository,
    private val articleMapper: ArticlePmMapper,
    private val idGenerator: IdGenerator,
) : ArticleRepository, ArticleIdGenerator {
    override fun save(newArticle: Article) {
        val articlePM = articleMapper.toPm(newArticle)
        jpaArticleRepository.save(articlePM)
    }

    override fun findById(articleId: ArticleId): Article? {
        TODO("Not yet implemented")
    }

    override fun generate(): ArticleId {
        return ArticleId(idGenerator.generate())
    }
}
