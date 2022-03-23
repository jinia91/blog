package myblog.blog.rss

import myblog.blog.article.domain.Article
import myblog.blog.article.application.ArticleService
import myblog.blog.seo.application.RssService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.lang.reflect.Field
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class RssServiceTests {

    @Mock
    lateinit var articleService: ArticleService
    @InjectMocks
    lateinit var rssService: RssService

    @Test
    fun `정상적으로 rssFeed를 가져오는 테스트`() {
        // given
        whenever(articleService.totalArticle)
                .thenReturn(Arrays.asList(buildArticle("테스트용", "1호", 1L), buildArticle("테스트용이에용", "2호", 2L)))
        // when
        val rssFeed = rssService.rssFeed
        // then
        firstArticleAssert(rssFeed)
        secondArticleAssert(rssFeed)
    }

    private fun buildArticle(title: String, content: String, id: Long): Article? {
        val article = Article.builder().title(title).content(content).build()
        setArticlePrivateFieldId(id, article)
        setArticleCreatedTimeStamp(article)
        return article
    }

    private fun setArticleCreatedTimeStamp(article: Article) {
        val clazz = article.javaClass.superclass
        val field: Field = clazz.getDeclaredField("createdDate")
        field.setAccessible(true)
        field.set(article, LocalDateTime.now())
    }

    private fun setArticlePrivateFieldId(id: Long, article: Article) {
        val clazz = article.javaClass
        val field: Field = clazz.getDeclaredField("id")
        field.setAccessible(true)
        field.set(article, id)
    }

    private fun firstArticleAssert(rssFeed: String?) {
        assertThat(rssFeed).contains("<title><![CDATA[테스트용]]></title>")
                .contains("<link>https://www.jiniaslog.co.kr/article/view?articleId=1</link>")
                .contains("<description><![CDATA[<p>1호</p>]]></description>")
                .contains("<guid>https://www.jiniaslog.co.kr/article/view?articleId=1</guid>")
    }

    private fun secondArticleAssert(rssFeed: String?) {
        assertThat(rssFeed).contains("<title><![CDATA[테스트용이에용]]></title>")
                .contains("<link>https://www.jiniaslog.co.kr/article/view?articleId=2</link>")
                .contains("<description><![CDATA[<p>2호</p>]]></description>")
                .contains("<guid>https://www.jiniaslog.co.kr/article/view?articleId=2</guid>")
    }
}