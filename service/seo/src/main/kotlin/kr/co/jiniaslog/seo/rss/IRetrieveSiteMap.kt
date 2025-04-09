package kr.co.jiniaslog.seo.rss

import kr.co.jiniaslog.seo.sitemap.SiteMap

interface IRetrieveSiteMap {
    fun handle(command: Command): Info

    class Command()

    data class Info(
        val siteMap: SiteMap
    )
}
