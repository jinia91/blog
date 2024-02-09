package kr.co.jiniaslog.blog.usecase.impl

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.outbound.MemoService
import kr.co.jiniaslog.blog.outbound.UserService
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.usecase.IPostNewArticle
import kr.co.jiniaslog.blog.usecase.UseCasesArticleFacade
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class UseCaseInteractor(
    private val memoService: MemoService,
    private val userService: UserService,
    private val articleRepository: ArticleRepository,
) : UseCasesArticleFacade {
    override fun handle(command: IPostNewArticle.Command): IPostNewArticle.Info {
        command.validate()
        val article =
            Article.newOne(
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
        memoRefId?.let { require(memoService.isExistMemo(this.memoRefId)) { "memo not found" } }
        require(userService.isExistUser(this.authorId)) { "user not found" }
        // todo category validation
    }
}
