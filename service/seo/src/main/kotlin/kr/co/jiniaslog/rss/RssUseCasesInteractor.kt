package kr.co.jiniaslog.rss

import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Description
import com.rometools.rome.feed.rss.Item
import kr.co.jiniaslog.blog.adapter.inbound.acl.BlogAclInboundAdapter
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import java.io.StringWriter
import java.time.ZoneId
import java.util.*

@UseCaseInteractor
class RssUseCasesInteractor(
    private val blogAclInboundAdapter: BlogAclInboundAdapter,
) : IRetrieveRssFeed {
    override fun handle(command: IRetrieveRssFeed.Command): IRetrieveRssFeed.Info {
        val articles = blogAclInboundAdapter.getAllArticle()
        val blogItems = articles.map {
            Item().apply {
                title = it.title
                link = "https://jiniaslog.co.kr/blog/${it.id}"
                description = Description().apply {
                    value = it.content
                }
                pubDate = Date.from(it.createdAt.toInstant(ZoneId.of("Asia/Seoul").rules.getOffset(it.createdAt)))
            }
        }

        val channel = Channel("rss_2.0").apply {
            title = "Jinia's Log"
            link = "https://jiniaslog.co.kr"
            description = "지니아의 종합 웹앱 플랫폼 사이트 입니다"
            pubDate = Date()
            items = blogItems
        }

        val output = com.rometools.rome.io.WireFeedOutput()
        val stringWriter = StringWriter()
        output.output(channel, stringWriter)
        val rssString = stringWriter.toString()
        return IRetrieveRssFeed.Info(rssString)
    }
}
