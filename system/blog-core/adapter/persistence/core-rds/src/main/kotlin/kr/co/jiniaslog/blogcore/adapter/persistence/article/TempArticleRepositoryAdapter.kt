package kr.co.jiniaslog.blogcore.adapter.persistence.article

import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import org.springframework.stereotype.Repository

@Repository
class TempArticleRepositoryAdapter(
    private val jpaTempArticleRepository: JpaTempArticleRepository,
    private val mapper: TempArticlePmMapper,
) : TempArticleRepository {
    override fun save(newArticle: TempArticle) {
        TODO("Not yet implemented")
    }

    override fun findTemp(articleId: ArticleId): TempArticle? {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }
}
