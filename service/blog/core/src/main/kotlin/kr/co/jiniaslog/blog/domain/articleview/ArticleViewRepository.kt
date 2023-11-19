package kr.co.jiniaslog.blog.domain.articleview

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.shared.core.domain.Repository

interface ArticleViewRepository : Repository<ArticleView, ArticleId> {
    @Deprecated("ArticleViewRepository.nextId() is not supported")
    override suspend fun nextId(): ArticleId {
        throw IllegalArgumentException("ArticleViewRepository.nextId() is not supported")
    }
}
