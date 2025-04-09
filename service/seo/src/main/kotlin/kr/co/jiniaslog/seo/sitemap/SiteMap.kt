package kr.co.jiniaslog.seo.sitemap

import kr.co.jiniaslog.blog.adapter.inbound.acl.ArticleAclVo
import org.jdom2.Document
import org.jdom2.Element
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SiteMap private constructor(val siteMapDoc: Document) {
    companion object {
        private const val NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9"
        private const val ROOT = "https://www.jiniaslog.co.kr"
        private const val BLOGPREV = "/blog/"

        fun from(articles: List<ArticleAclVo>): SiteMap {
            val doc = Document()
            val siteMap = Element("urlset", NAMESPACE)
            doc.setRootElement(siteMap)
            val main: Element = createMainElement()
            siteMap.addContent(main)
            addArticleUrlToSiteMap(articles, siteMap)
            return SiteMap(doc)
        }

        private fun createMainElement(): Element {
            val main = Element("url", NAMESPACE)
            main.addContent(Element("loc", NAMESPACE).setText(ROOT))
            main.addContent(
                Element("lastmod", NAMESPACE).setText(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
                )
            )
            main.addContent(Element("priority", NAMESPACE).setText("1.0"))
            return main
        }

        private fun addArticleUrlToSiteMap(articles: List<ArticleAclVo>, siteMap: Element) {
            for (article in articles) {
                val articleUrl: Element = Element("url", NAMESPACE)
                articleUrl.addContent(
                    Element("loc", NAMESPACE)
                        .setText(ROOT + BLOGPREV + article.id)
                )
                siteMap.addContent(articleUrl)
            }
        }
    }
}
