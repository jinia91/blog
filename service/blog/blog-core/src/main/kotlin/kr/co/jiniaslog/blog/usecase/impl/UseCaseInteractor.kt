package kr.co.jiniaslog.blog.usecase.impl

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.outbound.MemoAcl
import kr.co.jiniaslog.blog.outbound.UserAcl
import kr.co.jiniaslog.blog.usecase.IPostNewArticle
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class UseCaseInteractor(
//    private val memoService: MemoAcl,
//    private val userService: UserAcl,
    private val articleRepository: ArticleRepository
) : IPostNewArticle {
    override fun handle(command: IPostNewArticle.Command): IPostNewArticle.Info {
        command.validate()
        val article = Article.newOne(
            command.memoRefId,
            command.authorId,
            command.categoryId,
            command.articleContents,
            command.tags,
        )
        articleRepository.save(article)
        return IPostNewArticle.Info(article.id)
    }

    private fun IPostNewArticle.Command.validate() {
//        require(memoService.isExistMemo(this.memoRefId)) { "memo not found" }
//        require(userService.isExistUser(this.authorId)) { "user not found" }
    }
}

