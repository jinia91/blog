package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.ArticleContents
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.memo.MemoId
import kr.co.jiniaslog.blog.domain.tag.TagId
import kr.co.jiniaslog.blog.domain.user.UserId

interface IPostNewArticle {
    fun handle(command: Command): Info

    data class Command(
        val memoRefId: MemoId,
        val authorId: UserId,
        val categoryId: CategoryId,
        val articleContents: ArticleContents,
        val tags: List<TagId>,
    )

    data class Info(
        val id: ArticleId,
    )
}
