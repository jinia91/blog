package kr.co.jiniaslog.blogcore.adapter.persistence.article

import kr.co.jiniaslog.blogcore.domain.article.Article
import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.ArticleIdGenerator
import kr.co.jiniaslog.blogcore.domain.article.ArticleRepository
import kr.co.jiniaslog.shared.core.domain.ResourceNotFoundException
import kr.co.jiniaslog.shared.persistence.id.IdGenerator
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

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

    override fun findById(articleId: ArticleId): Article? =
        jpaArticleRepository.findById(articleId.value).getOrNull()?.toDomain()

    override fun delete(articleId: ArticleId) {
        val target = jpaArticleRepository.findById(articleId.value)
            .getOrElse { throw ResourceNotFoundException("$articleId is not found") }
        jpaArticleRepository.delete(target)
    }

    override fun generate(): ArticleId {
        return ArticleId(idGenerator.generate())
    }
}
