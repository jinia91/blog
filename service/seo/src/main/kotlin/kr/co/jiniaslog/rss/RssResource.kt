package kr.co.jiniaslog.rss

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/seo")
class RssResource(
    private val retrieveRssFeed: IRetrieveRssFeed
) {
    data class RssResponse(
        val feed: String
    )

    @GetMapping("/rss")
    fun getRssFeed(): ResponseEntity<RssResponse> {
        return ResponseEntity.ok(RssResponse(retrieveRssFeed.handle(IRetrieveRssFeed.Command()).feed))
    }
}
