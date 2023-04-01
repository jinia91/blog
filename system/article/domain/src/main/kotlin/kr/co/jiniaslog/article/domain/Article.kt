package kr.co.jiniaslog.article.domain

import kr.co.jiniaslog.lib.context.Domain

@Domain
class Article {
    val id: Long? = null
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
    var memberId: Long = 0
        private set
}
