package kr.co.jiniaslog.blogcore.adapter.persistence.article

import kr.co.jiniaslog.blogcore.application.article.infra.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.application.article.infra.ArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
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
