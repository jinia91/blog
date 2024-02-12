package kr.co.jiniaslog.blog.usecase.impl

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.outbound.MemoService
import kr.co.jiniaslog.blog.outbound.UserService
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.outbound.persistence.BlogTransactionHandler
import kr.co.jiniaslog.blog.usecase.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.IEditArticle
import kr.co.jiniaslog.blog.usecase.IPostNewArticle
import kr.co.jiniaslog.blog.usecase.UseCasesArticleFacade
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class ArticleUseCaseInteractor(
    private val memoService: MemoService,
    private val userService: UserService,
    private val articleRepository: ArticleRepository,
    private val transactionHandler: BlogTransactionHandler,
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
        transactionHandler.runInRepeatableReadTransaction {
            articleRepository.save(article)
        }
        return IPostNewArticle.Info(article.id)
    }

    private fun IPostNewArticle.Command.validate() {
        memoRefId?.let { require(memoService.isExistMemo(this.memoRefId)) { "memo not found" } }
        require(userService.isExistUser(this.authorId)) { "user not found" }
        // todo category validation
    }

    override fun handle(command: IEditArticle.Command): IEditArticle.Info {
        command.validate()
        val article = articleRepository.findById(command.articleId)
        requireNotNull(article) { "article not found" }

        article.edit(
            command.memoRefId,
            command.categoryId,
            command.articleContents,
            command.tags,
        )
        transactionHandler.runInRepeatableReadTransaction {
            articleRepository.save(article)
        }

        return IEditArticle.Info(article.id)
    }

    private fun IEditArticle.Command.validate() {
        memoRefId?.let { require(memoService.isExistMemo(this.memoRefId)) { "memo not found" } }
        // todo category validation
    }

    override fun handle(command: IDeleteArticle.Command): IDeleteArticle.Info {
        val article = articleRepository.findById(command.articleId)
        requireNotNull(article) { "article not found" }
        transactionHandler.runInRepeatableReadTransaction {
            articleRepository.deleteById(article.id)
        }
        return IDeleteArticle.Info()
    }

}

