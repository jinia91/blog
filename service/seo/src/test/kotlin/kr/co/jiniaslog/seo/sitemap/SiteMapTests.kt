package kr.co.jiniaslog.seo.sitemap

import io.kotest.matchers.string.shouldContain
import kr.co.jiniaslog.blog.adapter.inbound.acl.ArticleAclVo
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SiteMapTests : SimpleUnitTestContext() {
    @Test
    fun `아티클로 정상적인 사이트맵을 생성한다`() {
        // given
        val articles = listOf(
            ArticleAclVo(
                id = 1L,
                title = "title",
                content = "content",
                createdAt = LocalDateTime.now(),
            ),
            ArticleAclVo(
                id = 2L,
                title = "title",
                content = "content",
                createdAt = LocalDateTime.now(),
            ),
            ArticleAclVo(
                id = 3L,
                title = "title",
                content = "content",
                createdAt = LocalDateTime.now(),
            ),
        )

        // when
        val siteMap = SiteMap.from(articles)
        val xmlString = SiteMapResource.Companion.outPutter.outputString(siteMap.siteMapDoc)
        // then
        xmlString shouldContain "<loc>https://www.jiniaslog.co.kr</loc>"
        xmlString shouldContain "<priority>1.0</priority>"
        xmlString shouldContain "<loc>https://www.jiniaslog.co.kr/blog/1</loc>"
        xmlString shouldContain "<loc>https://www.jiniaslog.co.kr/blog/2</loc>"
        xmlString shouldContain "<loc>https://www.jiniaslog.co.kr/blog/3</loc>"
    }
}
