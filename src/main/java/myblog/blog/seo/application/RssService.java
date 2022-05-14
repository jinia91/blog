package myblog.blog.seo.application;

import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.seo.application.port.incomming.RssUseCase;
import myblog.blog.article.domain.Article;

import myblog.blog.seo.domain.RssFeed;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jdom2.output.*;
import java.util.*;

/*
        - rss 서비스 로직
          1. jdom2 사용하여 xml파일 작성
          2. 모든 게시물 item 변환후 xml에 삽입
          3. 발행
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RssService implements RssUseCase {
    private final ArticleUseCase articleUseCase;

    @Override
    @Cacheable(value = "seoCaching", key = "0")
    public String getRssFeed() {
        var articles = articleUseCase.getTotalArticle();
        var rssFeed = RssFeed.from(articles);
        var xmlOutputter = XMLOutPutterBuildHelper.getXmlOutputter();
        return xmlOutputter.outputString(rssFeed.getRssDoc());
    }
}
