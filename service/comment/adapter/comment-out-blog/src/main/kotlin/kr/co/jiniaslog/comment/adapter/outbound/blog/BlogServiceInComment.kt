package kr.co.jiniaslog.comment.adapter.outbound.blog

import kr.co.jiniaslog.blog.adapter.inbound.acl.BlogAclInboundAdapter
import kr.co.jiniaslog.comment.outbound.ArticleService
import kr.co.jiniaslog.shared.core.annotation.CustomComponent

@CustomComponent
class BlogServiceInComment(
    private val userQueries: BlogAclInboundAdapter,
) : ArticleService {
    override fun isExist(refId: Long): Boolean {
        return userQueries.isExist(refId)
    }
}
