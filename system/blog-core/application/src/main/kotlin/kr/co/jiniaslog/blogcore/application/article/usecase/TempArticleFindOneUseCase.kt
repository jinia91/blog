package kr.co.jiniaslog.blogcore.application.article.usecase

import kr.co.jiniaslog.blogcore.domain.article.TempArticle
import kr.co.jiniaslog.blogcore.domain.article.TempArticleId
import kr.co.jiniaslog.blogcore.domain.article.TempArticleRepository
import kr.co.jiniaslog.shared.core.context.UseCaseInteractor

interface TempArticleFindOneUseCase {
    fun findOne(): TempArticle?
}

@UseCaseInteractor
internal class TempArticleFindOneUseCaseInteractor(
    private val tempArticleRepository: TempArticleRepository,
) : TempArticleFindOneUseCase {
    override fun findOne(): TempArticle? =
        tempArticleRepository.getTemp(TempArticleId.getDefault())
}
