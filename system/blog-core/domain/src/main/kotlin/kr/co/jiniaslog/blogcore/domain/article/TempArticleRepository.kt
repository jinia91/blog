package kr.co.jiniaslog.blogcore.domain.article

interface TempArticleRepository {
    fun save(newTempArticle: TempArticle)
    fun getTemp(tempArticleId: TempArticleId): TempArticle?
    fun delete()
}
