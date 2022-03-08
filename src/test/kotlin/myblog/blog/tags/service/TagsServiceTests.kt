package myblog.blog.tags.service

import myblog.blog.article.domain.Article
import myblog.blog.tags.domain.ArticleTagList
import myblog.blog.tags.domain.Tags
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import myblog.blog.tags.repository.ArticleTagListsRepository
import myblog.blog.tags.repository.TagsRepository
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.withSettings
import java.lang.reflect.Method
import java.util.*
import kotlin.test.expect

@ExtendWith(MockitoExtension::class)
class TagsServiceTests {

    @Mock
    lateinit var tagsRepository: TagsRepository
    @Mock
    lateinit var articleTagListsRepository: ArticleTagListsRepository
    @InjectMocks
    lateinit var tagsService: TagsService

    @Test
    fun`기존 태그 존재시 조회하기`() {
        // given
        val article = Article.builder().build()
        val names = "[{\"value\":\"1\"},{\"value\":\"2\"}]"
        whenever(tagsRepository.findByName("1"))
            .thenReturn(Optional.of(Tags("1")))
        whenever(tagsRepository.findByName("2"))
            .thenReturn(Optional.of(Tags("2")))

        val mock1 = mock<ArticleTagListsRepository>(lenient = true){
            on {save(ArticleTagList(article, Tags("1"))) } doReturn ArticleTagList(article,Tags("1"))
        }
        val mock2 = mock<ArticleTagListsRepository>(lenient = true){
            on {save(ArticleTagList(article, Tags("2"))) } doReturn ArticleTagList(article,Tags("2"))
        }
    // when
        tagsService.createNewTagsAndArticleTagList(names, article);
    }

    @Test
    fun`신규 태그시 저장하기`() {
        // given
        val article = Article.builder().build()
        val names = "[{\"value\":\"1\"},{\"value\":\"2\"}]"
        whenever(tagsRepository.findByName("1"))
            .thenReturn(Optional.empty())

        val tags1 = Tags("1")
        val tags2 = Tags("2")

        val mock1 = mock<TagsRepository>(lenient = true){
            on {save(tags1) } doReturn tags1
        }
        val mock2 = mock<TagsRepository>(lenient = true){
            on {save(tags2) } doReturn tags2
        }
        val mock3 = mock<ArticleTagListsRepository>(lenient = true){
            on {save(ArticleTagList(article, tags1)) } doReturn ArticleTagList(article,tags1)
        }
        val mock4 = mock<ArticleTagListsRepository>(lenient = true){
            on {save(ArticleTagList(article, tags2)) } doReturn ArticleTagList(article,tags2)
        }
        // when
        tagsService.createNewTagsAndArticleTagList(names, article);
    }
}