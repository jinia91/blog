package myblog.blog.rss;

import myblog.blog.article.domain.Article;
import myblog.blog.article.service.ArticleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RssServiceTest {

    @InjectMocks
    RssService rssService;

    @Mock
    ArticleService articleService;

    @Test
    public void rss요청성공테스트() throws Exception {
        // given
        Mockito.when(articleService.getTotalArticle())
                .thenReturn(Arrays.asList(buildArticle("테스트용","1호",1L),buildArticle("테스트용이에용","2호",2L)));
        // when
        String rssFeed = rssService.getRssFeed();
        // then
        assertThat(rssFeed).contains("<title><![CDATA[테스트용]]></title>")
                .contains("<link>https://www.jiniaslog.co.kr/article/view?articleId=1</link>")
                .contains("<description><![CDATA[<p>1호</p>]]></description>")
                .contains("<guid>https://www.jiniaslog.co.kr/article/view?articleId=1</guid>");
        assertThat(rssFeed).contains("<title><![CDATA[테스트용이에용]]></title>")
                .contains("<link>https://www.jiniaslog.co.kr/article/view?articleId=2</link>")
                .contains("<description><![CDATA[<p>2호</p>]]></description>")
                .contains("<guid>https://www.jiniaslog.co.kr/article/view?articleId=2</guid>");
    }

    private Article buildArticle(String title, String content, Long id) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Article article = Article.builder().title(title).content(content).build();
        setArticlePrivateFieldId(id, article);
        setArticleCreatedTimeStamp(article);
        return article;
    }

    private void setArticleCreatedTimeStamp(Article article) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName("myblog.blog.article.domain.Article").getSuperclass();
        Field field = clazz.getDeclaredField("createdDate");
        field.setAccessible(true);
        field.set(article, LocalDateTime.now());
    }

    private void setArticlePrivateFieldId(Long id, Article article) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName("myblog.blog.article.domain.Article");
        Field field = clazz.getDeclaredField("id");
        field.setAccessible(true);
        field.set(article, id);
    }

}