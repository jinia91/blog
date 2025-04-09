package kr.co.jiniaslog.seo.rss

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/seo")
class RssResource(
    private val retrieveRssFeed: IRetrieveRssFeed
) {
    @GetMapping("/rss", produces = ["application/rss+xml"])
    fun getRssFeed(): ResponseEntity<String> {
        val rssXml = retrieveRssFeed.handle(IRetrieveRssFeed.Command()).feed
        return ResponseEntity.ok(rssXml)
    }
}
