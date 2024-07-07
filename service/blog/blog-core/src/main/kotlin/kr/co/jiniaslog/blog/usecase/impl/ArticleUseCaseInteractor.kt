package kr.co.jiniaslog.blog.usecase.impl

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.outbound.UserService
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.outbound.persistence.BlogTransactionHandler
import kr.co.jiniaslog.blog.usecase.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.IStartToWriteNewArticle
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class ArticleUseCaseInteractor(
    private val userService: UserService,
    private val articleRepository: ArticleRepository,
    private val transactionHandler: BlogTransactionHandler,
) : ArticleUseCasesFacade {
    override fun handle(command: IStartToWriteNewArticle.Command): IStartToWriteNewArticle.Info {
        command.validate()
        val article = command.toArticle()

        transactionHandler.runInRepeatableReadTransaction {
            articleRepository.save(article)
        }

        return IStartToWriteNewArticle.Info(article.id)
    }

    private fun IStartToWriteNewArticle.Command.validate() {
        require(userService.isExistUser(this.authorId)) { "유저가 존재하지 않습니다" }
    }

    private fun IStartToWriteNewArticle.Command.toArticle(): Article {
        return Article.newOne(this.authorId)
    }

    override fun handle(command: IDeleteArticle.Command): IDeleteArticle.Info {
        TODO("Not yet implemented")
    }
}
