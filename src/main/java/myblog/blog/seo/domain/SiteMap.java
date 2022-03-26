package myblog.blog.seo.domain;

import lombok.Getter;
import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import org.jdom2.Document;
import org.jdom2.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Getter
public class SiteMap {
    static private final String NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
    static private final String ROOT = "https://www.jiniaslog.co.kr";
    static private final String CATEGORYPRE = "/article/list?";
    static private final String CATEGORYPRO = "&page=1";
    static private final String ARTICLEPREV = "/article/view?articleId=";
    private final Document siteMapDoc;

    private SiteMap(Document siteMapDoc) {
        this.siteMapDoc = siteMapDoc;
    }

    static public SiteMap from(List<Article> articles, List<Category> allCategories) {
        Document doc = new Document();
        Element siteMap = new Element("urlset", NAMESPACE);
        doc.setRootElement(siteMap);
        Element main = createMainElement();
        siteMap.addContent(main);
        addCategoryUrlsToSiteMap(allCategories, siteMap);
        addArticleUrlToSiteMap(articles, siteMap);
        return new SiteMap(doc);
    }

    static private Element createMainElement() {
        Element main = new Element("url",NAMESPACE);
        main.addContent(new Element("loc",NAMESPACE).setText(ROOT));
        main.addContent(new Element("lastmod",NAMESPACE).setText(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))));
        main.addContent(new Element("priority",NAMESPACE).setText("1.0"));
        return main;
    }

    static private void addArticleUrlToSiteMap(List<Article> articles, Element siteMap) {
        for (Article article : articles) {
            Element articleUrl = new Element("url",NAMESPACE);
            articleUrl.addContent(new Element("loc",NAMESPACE)
                    .setText(ROOT + ARTICLEPREV + article.getId()));
            siteMap.addContent(articleUrl);
        }
    }

    static private void addCategoryUrlsToSiteMap(List<Category> allCategories, Element siteMap) {
        for (Category category : allCategories) {
            Element categoryUrl = new Element("url",NAMESPACE);
            categoryUrl.addContent(new Element("loc",NAMESPACE)
                    .setText(ROOT + CATEGORYPRE + "category="+category.getTitle()+"&tier="+category.getTier()+CATEGORYPRO));
            siteMap.addContent(categoryUrl);
        }
    }
}
