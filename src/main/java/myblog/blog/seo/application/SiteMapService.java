package myblog.blog.seo.application;

import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.article.application.port.incomming.SiteMapUseCase;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import myblog.blog.category.service.CategoryService;
import org.jdom2.*;
import org.jdom2.output.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SiteMapService implements SiteMapUseCase {
    
    static final String NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
    static final String ROOT = "https://www.jiniaslog.co.kr";
    static final String CATEGORYPRE = "/article/list?";
    static final String CATEGORYPRO = "&page=1";
    static final String ARTICLEPREV = "/article/view?articleId=";

    private final ArticleUseCase articleUseCase;
    private final CategoryService categoryService;

    @Override
    @Cacheable(value = "seoCaching", key = "1")
    public String getSiteMap(){
        List<Article> articles = articleUseCase.getTotalArticle();
        List<Category> allCategories = categoryService.getAllCategories();
        Document doc = makeSiteMapDocument(articles, allCategories);
        XMLOutputter xmlOutputter = getXmlOutputter();
        return xmlOutputter.outputString(doc);
    }

    private Document makeSiteMapDocument(List<Article> articles, List<Category> allCategories) {
        Document doc = new Document();
        Element siteMap = new Element("urlset", NAMESPACE);
        doc.setRootElement(siteMap);
        Element main = createMainElement();
        siteMap.addContent(main);
        addCategoryUrlsToSiteMap(allCategories, siteMap);
        addArticleUrlToSiteMap(articles, siteMap);
        return doc;
    }

    private Element createMainElement() {
        Element main = new Element("url",NAMESPACE);
        main.addContent(new Element("loc",NAMESPACE).setText(ROOT));
        main.addContent(new Element("lastmod",NAMESPACE).setText(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd",Locale.ENGLISH))));
        main.addContent(new Element("priority",NAMESPACE).setText("1.0"));
        return main;
    }

    private void addArticleUrlToSiteMap(List<Article> articles, Element siteMap) {
        for (Article article : articles) {
            Element articleUrl = new Element("url",NAMESPACE);
            articleUrl.addContent(new Element("loc",NAMESPACE)
                    .setText(ROOT + ARTICLEPREV + article.getId()));
            siteMap.addContent(articleUrl);
        }
    }

    private void addCategoryUrlsToSiteMap(List<Category> allCategories, Element siteMap) {
        for (Category category : allCategories) {
            Element categoryUrl = new Element("url",NAMESPACE);
            categoryUrl.addContent(new Element("loc",NAMESPACE)
                    .setText(ROOT + CATEGORYPRE + "category="+category.getTitle()+"&tier="+category.getTier()+CATEGORYPRO));
            siteMap.addContent(categoryUrl);
        }
    }

    // 쓰레드 세잎한지 확신이 안듬 방어적으로 생각해서 객체 생성하고 캐싱으로 처리하자
    private XMLOutputter getXmlOutputter() {
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        format.setLineSeparator("\r\n");
        return new XMLOutputter(format);
    }
}
