package kr.co.jiniaslog.blog

import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.memo.MemoId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.blog.usecase.IPostNewArticle
import org.junit.jupiter.api.Test

interface IPostNewArticleTestHelper {
    fun createMemo(): MemoId
    fun createAuthor(): UserId
    fun createCategory(): CategoryId
}

abstract class IPostNewArticleTests(
    val sut: IPostNewArticle,
    val helper: IPostNewArticleTestHelper,
) {

}
