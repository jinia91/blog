package kr.co.jiniaslog.blog.adapter.out.rdb.article

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleCommit
import kr.co.jiniaslog.blog.domain.article.ArticleCommitVersion
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.ArticleStagingSnapShot
import kr.co.jiniaslog.blog.domain.article.WriterId
import kr.co.jiniaslog.shared.core.annotation.CustomComponent

@CustomComponent
class ArticleFactory {
    fun assemble(
        articlePM: ArticlePM,
        commits: MutableList<ArticleCommit> = mutableListOf(),
        stagingSnapShot: ArticleStagingSnapShot? = null,
    ): Article {
        return Article.from(
            id = ArticleId(articlePM.id),
            writerId = WriterId(articlePM.writerId),
            head = ArticleCommitVersion(articlePM.head),
            articleHistory = commits,
            stagingSnapShot = stagingSnapShot,
            createdAt = articlePM.createdAt,
            updatedAt = articlePM.updatedAt,
            status = articlePM.status,
        )
    }
}
