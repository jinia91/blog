package kr.co.jiniaslog.blogcore.domain.article

import kr.co.jiniaslog.shared.core.context.DomainService

interface TempArticleService {
    fun saveTempArticle(tempArticle: TempArticle)
    fun findTempArticle(): TempArticle?
    fun deleteTempArticle()
}

@DomainService
internal class TempArticleServiceImpl(
    private val TempArticleRepository: TempArticleRepository,
) : TempArticleService {
    override fun saveTempArticle(tempArticle: TempArticle) {
        TempArticleRepository.save(tempArticle)
    }

    override fun findTempArticle(): TempArticle? =
        TempArticleRepository.findTemp(ArticleId(TempArticle.TEMP_ARTICLE_STATIC_ID))

    override fun deleteTempArticle() = TempArticleRepository.delete()
}
