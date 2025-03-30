package kr.co.jiniaslog.rss

interface IRetrieveRssFeed {
    fun handle(command: Command): Info

    class Command()

    data class Info(
        val feed: String
    )
}
