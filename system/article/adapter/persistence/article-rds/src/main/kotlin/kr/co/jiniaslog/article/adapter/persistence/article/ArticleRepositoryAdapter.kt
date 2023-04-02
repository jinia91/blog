package kr.co.jiniaslog.article.adapter.persistence.article

import kr.co.jiniaslog.article.application.port.ArticleRepository
import kr.co.jiniaslog.article.domain.Article
import kr.co.jiniaslog.article.domain.ArticleId
import org.springframework.stereotype.Repository

@Repository
class ArticleRepositoryAdapter(
    private val jpaArticleRepository: JpaArticleRepository,
    private val articleMapper: ArticlePmMapper,
) : ArticleRepository {
    override fun save(newArticle: Article) {
        jpaArticleRepository.save(
            ArticlePM(
                id = newArticle.id.value,
                title = newArticle.title,
                content = newArticle.content,
                hit = newArticle.hit,
                thumbnailUrl = newArticle.thumbnailUrl,
                writerId = newArticle.writerId.value,
            ),
        )
    }

    override fun findById(articleId: ArticleId): Article? {
        TODO("Not yet implemented")
    }
}
