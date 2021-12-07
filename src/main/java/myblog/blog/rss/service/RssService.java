package myblog.blog.rss.service;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RssService {

    private final HtmlRenderer htmlRenderer;
    private final Parser parser;

    public String makeRssFeed(List<Article> articles){

        Element rss = new Element("rss");
        rss.setAttribute(new Attribute("version","2.0"));
        Element channel = new Element("channel");
        rss.addContent(channel);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

        channel.addContent(new Element("title").addContent(new CDATA("Jinia's LOG")));
        channel.addContent(new Element("link").setText("http://218.38.91.194:10250/"));
        channel.addContent(new Element("description").addContent(new CDATA("비전공 출신, 개발자 지망생의 공부 내용을 기록하는 블로그입니다.")));

        for (Article article : articles) {

            Element item = new Element("item");

            item.addContent(new Element("title").addContent(new CDATA(article.getTitle())));
            item.addContent(new Element("link").setText("http://218.38.91.194:10250/article/view?articleId=" + article.getId()));
            item.addContent(new Element("description").addContent(new CDATA(htmlRenderer.render(parser.parse(article.getContent())))));
            item.addContent(new Element("pubDate").setText(dateFormat.format(Timestamp.valueOf(article.getCreatedDate()))));
            item.addContent(new Element("guid").setText("http://218.38.91.194:10250/article/view?articleId=" + article.getId()));

            channel.addContent(item);
        }

        Document doc = new Document();
        doc.setRootElement(rss);

        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        format.setLineSeparator("\r\n");

        XMLOutputter xmlOutputter = new XMLOutputter(format);

        return xmlOutputter.outputString(doc);

    }
}
