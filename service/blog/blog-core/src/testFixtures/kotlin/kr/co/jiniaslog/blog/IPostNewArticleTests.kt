package kr.co.jiniaslog.blog

import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.memo.MemoId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.blog.usecase.IPostNewArticle

interface IPostNewArticleTestHelper {
    fun createMemo(): MemoId

    fun createAuthor(): UserId

    fun createCategory(): CategoryId
}

abstract class IPostNewArticleTests(
    val sut: IPostNewArticle,
    val helper: IPostNewArticleTestHelper,
)
