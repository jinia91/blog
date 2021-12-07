package myblog.blog.rss.controller;

import com.rometools.rome.feed.rss.Channel;
import lombok.RequiredArgsConstructor;
import myblog.blog.article.service.ArticleService;
import myblog.blog.rss.service.RssService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class RssController {

    private final RssService rssService;
    private final ArticleService articleService;

    @GetMapping(value = "/rss",produces = "application/xml;charset=utf-8")
    public @ResponseBody String rssFeed() {

        return rssService.makeRssFeed(articleService.getTotalArticle());

    }
}
