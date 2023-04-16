package kr.co.jiniaslog.blogcore.adapter.persistence.article

import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleId
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Repository
internal class TempArticleRepositoryAdapter(
    private val tempArticleJpaRepository: TempArticleJpaRepository,
) : TempArticleRepository {
    override fun save(newTempArticle: TempArticle) {
        tempArticleJpaRepository.save(
            newTempArticle.toPm()
                .apply { createdDate = LocalDateTime.now() },
        )
    }

    override fun getTemp(tempArticleId: TempArticleId): TempArticle? = tempArticleJpaRepository.findById(tempArticleId.value)
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

    private fun TempArticlePM.toDomain(): TempArticle = TempArticle.Factory.from(
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = UserId(writerId),
        categoryId = categoryId?.let { CategoryId(it) },
    )
}
