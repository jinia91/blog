package kr.co.jiniaslog.article.domain

import kr.co.jiniaslog.lib.context.DomainEntity

@DomainEntity
class Article {
    val id: ArticleId = ArticleId(0)
    var title: String = ""
        private set
    var content: String = ""
        private set
    var hit: Long = 0
        private set
    var toc: String = ""
        private set
    var thumbnailUrl: String = ""
        private set
    var writerId: Long = 0
        private set
}
