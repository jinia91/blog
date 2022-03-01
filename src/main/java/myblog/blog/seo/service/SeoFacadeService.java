package myblog.blog.seo.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.service.ArticleService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoFacadeService {

    private final RssService rssService;
    private final SiteMapService siteMapService;
    private final ArticleService articleService;

    @Cacheable(value = "seoCaching", key = "0")
    public String getRssFeed(){
        List<Article> articles = articleService.getTotalArticle();
        return rssService.getRssFeed(articles);
    }

}
