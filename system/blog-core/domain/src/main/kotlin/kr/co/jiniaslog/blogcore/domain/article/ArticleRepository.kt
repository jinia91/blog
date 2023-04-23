package kr.co.jiniaslog.blogcore.domain.article

interface ArticleRepository {
    fun save(newArticle: Article)

    fun findById(articleId: ArticleId): Article?

    fun delete(articleId: ArticleId)
}
