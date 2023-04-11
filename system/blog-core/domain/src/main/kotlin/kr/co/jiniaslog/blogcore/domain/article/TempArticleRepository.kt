package kr.co.jiniaslog.blogcore.domain.article

interface TempArticleRepository {
    fun save(newArticle: TempArticle)
    fun findTemp(articleId: ArticleId): TempArticle?
    fun delete()
}
