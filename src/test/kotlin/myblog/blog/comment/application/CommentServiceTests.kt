package myblog.blog.comment.application

import myblog.blog.article.application.port.incomming.ArticleUseCase
import myblog.blog.comment.application.port.outgoing.CommentRepositoryPort
import myblog.blog.comment.domain.Comment
import myblog.blog.comment.domain.NotFoundParentCommnetException
import myblog.blog.member.application.port.incomming.MemberQueriesUseCase
import myblog.blog.member.doamin.Member
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CommentServiceTests {

    @Mock
    lateinit var articleUseCase: ArticleUseCase
    @Mock
    lateinit var memberQueriesUseCase: MemberQueriesUseCase
    @Mock
    lateinit var commentRepositoryPort: CommentRepositoryPort
    @InjectMocks
    lateinit var commentService: CommentService

    @Test
    fun `부모 댓글 저장 성공`(){
        whenever(memberQueriesUseCase.findById(1L)).thenReturn(Optional.of(Member()))
        //when
        commentService.savePComment("부모 댓글", false, 1L, 1L)
        //then
        val forClass = ArgumentCaptor.forClass(Comment::class.java)
        verify(commentRepositoryPort, times(1)).save(forClass.capture())
    }

    @Test
    fun `자식 댓글 저장 성공`(){
        //given
        val commentCaptor = ArgumentCaptor.forClass(Comment::class.java)
        whenever(memberQueriesUseCase.findById(1L)).thenReturn(Optional.of(Member()))
        val comment = Comment.builder().content("test").pOrder(1).build()
        //when
        whenever(commentRepositoryPort.findById(1L)).thenReturn(Optional.of(comment))
        commentService.saveCComment("자식 댓글", false, 1L, 1L, 1L)
        //then
        verify(commentRepositoryPort, times(1)).save(commentCaptor.capture())
    }

    @Test
    fun `부모가 없는 자식댓글 에러`(){
        whenever(memberQueriesUseCase.findById(1L)).thenReturn(Optional.of(Member()))
        assertFailsWith<NotFoundParentCommnetException> {
            commentService.saveCComment("자식 댓글", false, 1L, 1L, 1L)
        }
    }

    @Test
    fun `댓글 삭제 성공`(){
        //given
        //when
        commentService.deleteComment(1L)
        //then
        verify(commentRepositoryPort, times(1)).deleteComment(1L)
    }

}