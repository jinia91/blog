package kr.co.jiniaslog.blogcore.adapter.persistence.article

import kr.co.jiniaslog.blogcore.domain.article.ArticleId
import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.blogcore.domain.article.UserId
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
internal class TempArticleRepositoryAdapter(
    private val tempArticleJpaRepository: TempArticleJpaRepository,
) : TempArticleRepository {
    override fun save(newTempArticle: TempArticle) {
        tempArticleJpaRepository.save(newTempArticle.toPm())
    }

    override fun getTemp(articleId: ArticleId): TempArticle? = tempArticleJpaRepository.findById(articleId.value)
        .getOrNull()?.toDomain()

    override fun delete() = tempArticleJpaRepository.deleteAll()

    private fun TempArticle.toPm(): TempArticlePM = TempArticlePM(
        id = id.value,
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = writerId.value,
        categoryId = categoryId?.value,
    )

    private fun TempArticlePM.toDomain(): TempArticle = TempArticle(
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = UserId(writerId),
        categoryId = categoryId?.let { CategoryId(it) },
    )
}
