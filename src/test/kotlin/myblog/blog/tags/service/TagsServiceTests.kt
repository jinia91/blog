package myblog.blog.tags.service

import myblog.blog.article.domain.Article
import myblog.blog.tags.domain.ArticleTagList
import myblog.blog.tags.domain.Tags
import myblog.blog.tags.repository.ArticleTagListsRepository
import myblog.blog.tags.repository.TagsRepository

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
class TagsServiceTests {

    @Mock
    lateinit var tagsRepository: TagsRepository
    @Mock
    lateinit var articleTagListsRepository: ArticleTagListsRepository
    @InjectMocks
    lateinit var tagsService: TagsService

    @Test
    fun`기존 태그 존재시 성공적으로 tagList 저장하기`() {
        // given
        val article = Article.builder().build()
        val names = "[{\"value\":\"1\"},{\"value\":\"2\"}]"
//        whenever(tagsRepository.findByName("1"))
//            .thenReturn(Optional.of(Tags("1")))
//        whenever(tagsRepository.findByName("2"))
//            .thenReturn(Optional.of(Tags("2")))
        // when
        tagsService.createNewTagsAndArticleTagList(names, article);
        //then
        val capturedArticleTagList = ArgumentCaptor.forClass(ArticleTagList::class.java)
        verify(articleTagListsRepository, times(2)).save(capturedArticleTagList.capture())
    }

    @Test
    fun`신규 태그시 태그 저장후 성공적으로 tagList도 저장하기`() {
        // given
        val article = Article.builder().build()
        val names = "[{\"value\":\"1\"},{\"value\":\"2\"}]"
//        whenever(tagsRepository.findByName("1"))
//            .thenReturn(Optional.empty())

        val tags1 = Tags("1")
        val tags2 = Tags("2")

//        val mock1 = mock<TagsRepository>(lenient = true){
//            on {save(tags1) } doReturn tags1
//        }
//        val mock2 = mock<TagsRepository>(lenient = true){
//            on {save(tags2) } doReturn tags2
//        }
        // when
        tagsService.createNewTagsAndArticleTagList(names, article);
        //then
        val capturedArticleTagList = ArgumentCaptor.forClass(ArticleTagList::class.java)
        verify(articleTagListsRepository, times(2)).save(capturedArticleTagList.capture())
    }

    @Test
    fun `아티클 관련 태그 전부 삭제 성공`(){
    //given
        val article = Article.builder().build()
    //when
        tagsService.deleteAllTagsWith(article)
    //then
        verify(articleTagListsRepository, times(1)).deleteByArticle(article)
    }
}