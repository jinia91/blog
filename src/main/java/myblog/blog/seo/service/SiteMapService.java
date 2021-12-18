package myblog.blog.seo.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.domain.Category;
import myblog.blog.category.service.CategoryService;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
@RequiredArgsConstructor
public class SiteMapService {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    static final String NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
    static final String ROOT = "https://www.jiniaslog.co.kr";
    static final String CATEGORYPRE = "/article/list?";
    static final String CATEGORYPRO = "&page=1";
    static final String ARTICLEPREV = "/article/view?articleId=";

    /*
        - 사이트맵 작성 로직
    */
    @Cacheable(value = "seoCaching", key = "1")
    public String makeSiteMap(){
        List<Article> articles = articleService.getTotalArticle();
        List<Category> allCategories = categoryService.getAllCategories();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.ENGLISH);

        // siteMap 레이아웃 루트 작성
        Element siteMap = new Element("urlset", NAMESPACE);

        // 메인화면
        Element main = new Element("url",NAMESPACE);
        main.addContent(new Element("loc",NAMESPACE).setText(ROOT));
        main.addContent(new Element("lastmod",NAMESPACE).setText(fmt.format(new Date())));
        main.addContent(new Element("priority",NAMESPACE).setText("1.0"));
        siteMap.addContent(main);

        // url 삽입
        addCategoryUrl(allCategories, siteMap);
        addArticleUrl(articles, siteMap);

        Document doc = new Document();
        doc.setRootElement(siteMap);

        XMLOutputter xmlOutputter = getXmlOutputter();

        return xmlOutputter.outputString(doc);

    }

    /*
    - 아티클 url 삽입
    */
    private void addArticleUrl(List<Article> articles, Element siteMap) {
        for (Article article : articles) {
            Element articleUrl = new Element("url",NAMESPACE);
            articleUrl.addContent(new Element("loc",NAMESPACE)
                    .setText(ROOT + ARTICLEPREV + article.getId()));
            siteMap.addContent(articleUrl);
        }
    }

    /*
    - 카테고리별 url 삽입
    */
    private void addCategoryUrl(List<Category> allCategories, Element siteMap) {
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
