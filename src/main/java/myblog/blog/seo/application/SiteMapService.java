package myblog.blog.seo.application;

import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.seo.application.port.incomming.SiteMapUseCase;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import myblog.blog.seo.domain.SiteMap;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.jdom2.output.*;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SiteMapService implements SiteMapUseCase {
    private final ArticleUseCase articleUseCase;
    private final CategoryUseCase categoryUseCase;

    @Override
    @Cacheable(value = "seoCaching", key = "1")
    public String getSiteMap(){
        var articles = articleUseCase.getTotalArticle();
        var allCategories = categoryUseCase.getAllCategories();
        var siteMap = SiteMap.from(articles, allCategories);
        var xmlOutputter = XMLOutPutterBuildHelper.getXmlOutputter();
        return xmlOutputter.outputString(siteMap.getSiteMapDoc());
    }
}
