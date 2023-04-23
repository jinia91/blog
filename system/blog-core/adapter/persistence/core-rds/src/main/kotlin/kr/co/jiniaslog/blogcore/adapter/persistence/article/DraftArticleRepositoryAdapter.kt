package kr.co.jiniaslog.blogcore.adapter.persistence.article

import kr.co.jiniaslog.blogcore.domain.category.CategoryId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticle
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleId
import kr.co.jiniaslog.blogcore.domain.draft.DraftArticleRepository
import kr.co.jiniaslog.blogcore.domain.user.UserId
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Repository
internal class DraftArticleRepositoryAdapter(
    private val tempArticleJpaRepository: TempArticleJpaRepository,
) : DraftArticleRepository {
    override fun save(newDraftArticle: DraftArticle) {
        tempArticleJpaRepository.save(
            newDraftArticle.toPm()
                .apply { createdDate = LocalDateTime.now() },
        )
    }

    override fun getById(draftArticleId: DraftArticleId): DraftArticle? = tempArticleJpaRepository.findById(draftArticleId.value)
        .getOrNull()?.toDomain()

    override fun delete() = tempArticleJpaRepository.deleteAll()

    private fun DraftArticle.toPm(): TempArticlePM = TempArticlePM(
        id = id.value,
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = writerId.value,
        categoryId = categoryId?.value,
    )

    private fun TempArticlePM.toDomain(): DraftArticle = DraftArticle.Factory.from(
        title = title,
        content = content,
        thumbnailUrl = thumbnailUrl,
        writerId = UserId(writerId),
        categoryId = categoryId?.let { CategoryId(it) },
    )
}
