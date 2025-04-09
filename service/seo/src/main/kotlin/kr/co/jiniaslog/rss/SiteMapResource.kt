package kr.co.jiniaslog.rss

import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/seo")
class SiteMapResource(
    private val siteMapUseCase: IRetrieveSiteMap,
) {
    @GetMapping("/sitemap", produces = ["application/xml;charset=utf-8"])
    fun getSiteMap(): ResponseEntity<String> {
        val command = IRetrieveSiteMap.Command()
        val siteMap = siteMapUseCase.handle(command).siteMap
        val siteMapXml = outPutter.outputString(siteMap.siteMapDoc)
        return ResponseEntity.ok(siteMapXml)
    }

    companion object {
        val outPutter: XMLOutputter
            get() {
                val format = Format.getPrettyFormat()
                format.setEncoding("UTF-8")
                format.setLineSeparator("\r\n")
                return XMLOutputter(format)
            }
    }
}
