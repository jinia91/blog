package myblog.blog.seo.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.service.ArticleService;
import myblog.blog.seo.service.RssService;
import myblog.blog.seo.service.SeoFacadeService;
import myblog.blog.seo.service.SiteMapService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SEOController {

    private final SeoFacadeService seoFacadeService;
    private final SiteMapService siteMapService;

    /*
        - rss 피드 발행 요청
          - rss 발행 비용을 감안해 캐시처리
          - 발행빈도가 아직 예측되지않으므로 캐시 만료를 12시간으로 설정
    */
    @GetMapping(value = "/rss",produces = "application/xml;charset=utf-8")
    public @ResponseBody String rssFeed() {
        return seoFacadeService.getRssFeed();
    }

    /*
        - siteMap.xml 요청
          - 작성 비용을 감안해 캐시처리
          - 조회 빈도가 아직 예측되지않으므로 캐시 만료를 12시간으로 설정
    */
    @GetMapping(value = "/sitemap",produces = "application/xml;charset=utf-8")
    public @ResponseBody String getSiteMap() {
        return siteMapService.makeSiteMap();
    }

}
