package kr.co.jiniaslog.rss

interface IRetrieveSiteMap {
    fun handle(command: Command): Info

    class Command()

    data class Info(
        val siteMap: SiteMap
    )
}
