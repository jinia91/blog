package myblog.blog.seo.domain;

import lombok.Getter;

import myblog.blog.article.domain.Article;

import org.jdom2.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static myblog.blog.shared.utils.MarkdownUtils.getHtmlRenderer;
import static myblog.blog.shared.utils.MarkdownUtils.getParser;

@Getter
public class RssFeed {
    static private final String ITEM_ROOT = "https://www.jiniaslog.co.kr/article/view?articleId=";
    private final Document rssDoc;

    private RssFeed(Document rssDoc) {
        this.rssDoc = rssDoc;
    }

    static public RssFeed from(List<Article> articles){
        Document doc = new Document();
        Element rss = buildRssRoot();
        doc.setRootElement(rss);
        Element channel = buildChannel();
        rss.addContent(channel);
        addArticleItemsToChannel(articles, channel, new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH));
        return new RssFeed(doc);
    }

    static private Element buildRssRoot() {
        Element rss = new Element("rss");
        rss.setAttribute(new Attribute("version", "2.0"));
        return rss;
    }

    static private Element buildChannel() {
        Element channel = new Element("channel");
        channel.addContent(new Element("title").addContent(new CDATA("Jinia's LOG")));
        channel.addContent(new Element("link").setText("https://www.jiniaslog.co.kr"));
        channel.addContent(new Element("description").addContent(new CDATA("비전공 출신, 개발자 지망생의 공부 내용을 기록하는 블로그입니다.")));
        return channel;
    }

    static private void addArticleItemsToChannel(List<Article> articles, Element channel, SimpleDateFormat dateFormat) {
        for (Article article : articles) {
            Element item = createItemTo(article, dateFormat);
            channel.addContent(item);
        }
    }

    static private Element createItemTo(Article article, SimpleDateFormat dateFormat) {
        Element item = new Element("item");
        item
                .addContent(new Element("title").addContent(new CDATA(article.getTitle())))
                .addContent(new Element("link").setText(ITEM_ROOT + article.getId()))
                .addContent(new Element("description").addContent(new CDATA(getHtmlRenderer().render(getParser().parse(article.getContent())))))
                .addContent(new Element("pubDate").setText(dateFormat.format(Timestamp.valueOf(article.getCreatedDate()))))
                .addContent(new Element("guid").setText(ITEM_ROOT + article.getId()));
        return item;
    }

}
