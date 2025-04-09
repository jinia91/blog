package kr.co.jiniaslog.seo.sitemap

import kr.co.jiniaslog.blog.adapter.inbound.acl.BlogAclInboundAdapter
import kr.co.jiniaslog.seo.rss.IRetrieveSiteMap
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class SiteMapCasesInteractor(
    private val blogAclInboundAdapter: BlogAclInboundAdapter,
) : IRetrieveSiteMap {
    override fun handle(command: IRetrieveSiteMap.Command): IRetrieveSiteMap.Info {
        val articles = blogAclInboundAdapter.getAllArticle()
        val siteMap = SiteMap.from(articles)
        return IRetrieveSiteMap.Info(siteMap)
    }
}
