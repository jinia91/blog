package kr.co.jiniaslog

import io.kotest.matchers.shouldNotBe
import java.awt.SystemColor.info
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.memo.MemoId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.usecase.IPostNewArticle
import kr.co.jiniaslog.memo.domain.memo.AuthorId
import kr.co.jiniaslog.memo.domain.memo.Memo
import kr.co.jiniaslog.memo.outbound.MemoRepository
import kr.co.jiniaslog.user.application.infra.UserRepository
import kr.co.jiniaslog.user.domain.user.Email
import kr.co.jiniaslog.user.domain.user.NickName
import kr.co.jiniaslog.user.domain.user.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.testcontainers.shaded.org.bouncycastle.asn1.x500.style.RFC4519Style.title

abstract class BlogUseCasesIntegrationTestsSuite {
    @Autowired
    private lateinit var sut: IPostNewArticle

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var articleRepository: ArticleRepository

    @Autowired
    private lateinit var memoRepository: MemoRepository


    @Test
    fun `참조할 메모 없이 유효한 데이터로 게시글 생성 요청을 하면 게시글이 생성된다`() {
        // given
        val user = userRepository.save(
            User.newOne(
                NickName("jinia"),
                Email("jinia@google.com"),
            ),
        )

        val command = IPostNewArticle.Command(
            authorId = UserId(user.id.value),
            categoryId = CategoryId(1L),
            articleContents = ArticleContents(
                title = "title",
                contents = "body",
                thumbnailUrl = "thumbnailUrl",
            ),
            tags = emptyList(),
        )

        // when
        val info = sut.handle(command)

        // then
        info shouldNotBe null
        info.articleId shouldNotBe null

        // 상태 검증
        val article = articleRepository.findById(info.articleId)
        article shouldNotBe null
    }

    @Test
    fun `참조할 메모가 있고 유효한 데이터로 게시글 생성 요청을 하면 게시글이 생성된다`() {
        // given
        val user = userRepository.save(
            User.newOne(
                NickName("jinia"),
                Email("jinia@google.com"),
            ),
        )

        val memo = memoRepository.save(
            Memo.init(
                authorId = AuthorId(user.id.value),
                parentFolderId = null
            ),
        )

        val command = IPostNewArticle.Command(
            memoRefId = MemoId(memo.id.value),
            authorId = UserId(user.id.value),
            categoryId = CategoryId(1L),
            articleContents = ArticleContents(
                title = "title",
                contents = "body",
                thumbnailUrl = "thumbnailUrl",
            ),
            tags = emptyList(),
        )

        // when
        val info = sut.handle(command)

        // then
        info shouldNotBe null
        info.articleId shouldNotBe null

        // 상태 검증
        val article = articleRepository.findById(info.articleId)
        article shouldNotBe null
    }
}
