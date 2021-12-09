package myblog.blog.seo.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.service.ArticleService;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RssService {

    private final HtmlRenderer htmlRenderer;
    private final Parser parser;
    private final ArticleService articleService;

    /*
        - rss 빌드 서비스 로직
          1. 모든 게시물 조회
          2. jdom2 사용하여 xml파일 작성
          3. 모든 게시물 item 변환후 xml에 삽입
          4. 발행
    */
    @Cacheable(value = "seoCaching", key = "0")
    public String makeRssFeed(){

        List<Article> articles = articleService.getTotalArticle();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        // rss 레이아웃 루트 작성
        Element rss = new Element("rss");
        rss.setAttribute(new Attribute("version","2.0"));

        // 채널 작성 밑 루트에 삽입
        Element channel = buildChannel();
        rss.addContent(channel);

        // 채널에 아이템 삽입
        addItemToChannel(articles, dateFormat, channel);

        // 포매팅해서 발행
        Document doc = new Document();
        doc.setRootElement(rss);
        XMLOutputter xmlOutputter = getXmlOutputter();

        return xmlOutputter.outputString(doc);
    }

    /*
        - 채널 작성 로직
    */
    private Element buildChannel() {
        Element channel = new Element("channel");
        channel.addContent(new Element("title").addContent(new CDATA("Jinia's LOG")));
        channel.addContent(new Element("link").setText("http://218.38.91.194:10250/"));
        channel.addContent(new Element("description").addContent(new CDATA("비전공 출신, 개발자 지망생의 공부 내용을 기록하는 블로그입니다.")));
        return channel;
    }

    /*
        - xml 발행기 생성
    */
    private XMLOutputter getXmlOutputter() {
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        format.setLineSeparator("\r\n");
        return new XMLOutputter(format);
    }

    /*
        - item 작성 , 채널에 추가
    */
    private void addItemToChannel(List<Article> articles, SimpleDateFormat dateFormat, Element channel) {
        for (Article article : articles) {

            Element item = new Element("item");

            item.addContent(new Element("title").addContent(new CDATA(article.getTitle())));
            item.addContent(new Element("link").setText("http://218.38.91.194:10250/article/view?articleId=" + article.getId()));
            item.addContent(new Element("description").addContent(new CDATA(htmlRenderer.render(parser.parse(article.getContent())))));
            item.addContent(new Element("pubDate").setText(dateFormat.format(Timestamp.valueOf(article.getCreatedDate()))));
            item.addContent(new Element("guid").setText("http://218.38.91.194:10250/article/view?articleId=" + article.getId()));

            channel.addContent(item);
        }
    }
}
