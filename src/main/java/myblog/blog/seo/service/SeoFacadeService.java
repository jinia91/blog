package myblog.blog.seo.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.domain.Category;
import myblog.blog.category.service.CategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoFacadeService {

    private final RssService rssService;
    private final SiteMapService siteMapService;
    private final ArticleService articleService;
    private final CategoryService categoryService;

    @Cacheable(value = "seoCaching", key = "0")
    public String getRssFeed(){
        List<Article> articles = articleService.getTotalArticle();
        return rssService.getRssFeed(articles);
    }

    @Cacheable(value = "seoCaching", key = "1")
    public String getSiteMap(){
        List<Category> allCategories = categoryService.getAllCategories();
        List<Article> articles = articleService.getTotalArticle();
        return siteMapService.getSiteMap(articles,allCategories);
    }

}
