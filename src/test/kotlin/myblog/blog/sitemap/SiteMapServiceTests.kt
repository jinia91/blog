package myblog.blog.sitemap

import myblog.blog.article.domain.Article
import myblog.blog.article.application.ArticleService
import myblog.blog.article.application.SiteMapService
import myblog.blog.category.domain.Category
import myblog.blog.category.service.CategoryService
import myblog.blog.rss.RssService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
//import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.lang.reflect.Field
import java.time.LocalDateTime
import java.util.*


@ExtendWith(MockitoExtension::class)
class SiteMapServiceTests {

    @Mock
    lateinit var articleService: ArticleService
    @Mock
    lateinit var categoryService: CategoryService
    @InjectMocks
    lateinit var siteMapService: SiteMapService

    @Test
    fun `정상적으로 siteMap을 가져오는 테스트`() {
        // given
        whenever(articleService.totalArticle)
                .thenReturn(Arrays.asList(buildArticle("테스트용", "1호", 1L), buildArticle("테스트용이에용", "2호", 2L)))
        whenever(categoryService.allCategories)
                .thenReturn(Arrays.asList(buildCategory("목 카테고리")))
        // when
        val siteMap = siteMapService.siteMap
        // then
        siteMapRootBuildlAssert(siteMap)
        siteMapCategoryUrlBuildAssert(siteMap)
        siteMapArticleUrlBuildAssert(siteMap)
    }

    private fun buildCategory(title: String) = Category.builder().title(title).tier(1).build()
    private fun buildArticle(title: String, content: String, id: Long): Article? {
        val article = Article.builder().title(title).content(content).build()
        setArticlePrivateFieldId(id, article)
        setArticleCreatedTimeStamp(article)
        return article
    }

    private fun setArticleCreatedTimeStamp(article: Article) {
        val clazz = Class.forName("myblog.blog.article.domain.Article").superclass
        val field: Field = clazz.getDeclaredField("createdDate")
        field.setAccessible(true)
        field.set(article, LocalDateTime.now())
    }

    private fun setArticlePrivateFieldId(id: Long, article: Article) {
        val clazz = Class.forName("myblog.blog.article.domain.Article")
        val field: Field = clazz.getDeclaredField("id")
        field.setAccessible(true)
        field.set(article, id)
    }

    private fun siteMapRootBuildlAssert(rssFeed: String?) {
        assertThat(rssFeed).contains("<loc>https://www.jiniaslog.co.kr</loc>")
                .contains("<priority>1.0</priority>")
    }

    private fun siteMapCategoryUrlBuildAssert(rssFeed: String?) {
        assertThat(rssFeed).contains("<loc>https://www.jiniaslog.co.kr/article/list?category=목 카테고리&amp;tier=1&amp;page=1</loc>")
    }

    private fun siteMapArticleUrlBuildAssert(rssFeed: String?) {
        assertThat(rssFeed).contains("<loc>https://www.jiniaslog.co.kr/article/view?articleId=1</loc>")
                .contains("<loc>https://www.jiniaslog.co.kr/article/view?articleId=2</loc>")
    }
}