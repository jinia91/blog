package myblog.blog.comment.domain

import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertFailsWith

class CommentTests {

    @Test
    fun `빈 댓글 작성시 에러`(){
        assertFailsWith<IllegalArgumentException> {
            Comment.builder().build()
        }
    }
}