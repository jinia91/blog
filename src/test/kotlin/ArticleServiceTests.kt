package myblog.blog.article.application

import myblog.blog.article.application.port.incomming.TagUseCase
import myblog.blog.article.application.port.incomming.request.ArticleCreateCommand
import myblog.blog.article.application.port.outgoing.ArticleBackupRepositoryPort
import myblog.blog.article.application.port.outgoing.ArticleRepositoryPort
import myblog.blog.article.domain.Article
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase
import myblog.blog.category.domain.Category
import myblog.blog.member.application.port.incomming.MemberQueriesUseCase
import myblog.blog.member.doamin.Member
import myblog.blog.shared.domain.BadRequestException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ArticleServiceTests {
    @Mock lateinit var tagUseCase: TagUseCase
    @Mock lateinit var categoryUseCase: CategoryUseCase
    @Mock lateinit var memberQueriesUseCase: MemberQueriesUseCase
    @Mock lateinit var articleRepositoryPort: ArticleRepositoryPort
    @Mock lateinit var articleBackupRepositoryPort: ArticleBackupRepositoryPort
    @InjectMocks lateinit var sut: ArticleService

    @Test
    fun`제목이 없는 article 글등록 불가`(){
        val articleCreateCommand = ArticleCreateCommand(1L,null,"content",null,null,"category","tags")
        whenever(memberQueriesUseCase.findById(articleCreateCommand.memberId))
            .thenReturn(Optional.of(Member()))
        whenever(categoryUseCase.findCategory(articleCreateCommand.category))
            .thenReturn(Optional.of(Category()))
        assertFailsWith<BadRequestException> {
            sut.writeArticle(articleCreateCommand)
        }
    }

    @Test
    fun`내용이 없는 article 글등록 불가`(){
        val articleCreateCommand = ArticleCreateCommand(1L,"title",null,null,null,"category","tags")
        whenever(memberQueriesUseCase.findById(articleCreateCommand.memberId))
            .thenReturn(Optional.of(Member()))
        whenever(categoryUseCase.findCategory(articleCreateCommand.category))
            .thenReturn(Optional.of(Category()))
        assertFailsWith<BadRequestException> {
            sut.writeArticle(articleCreateCommand)
        }
    }

    @Test
    fun`article 등록 성공`(){
        val articleCreateCommand = ArticleCreateCommand(1L,"title","content",null,null,"category","tags")
        whenever(memberQueriesUseCase.findById(articleCreateCommand.memberId))
            .thenReturn(Optional.of(Member()))
        whenever(categoryUseCase.findCategory(articleCreateCommand.category))
            .thenReturn(Optional.of(Category()))
        sut.writeArticle(articleCreateCommand)
    }


}