package kr.co.jiniaslog.blog.usecase.impl

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.article.QArticle.article
import kr.co.jiniaslog.blog.outbound.UserService
import kr.co.jiniaslog.blog.outbound.persistence.ArticleRepository
import kr.co.jiniaslog.blog.outbound.persistence.BlogTransactionHandler
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class ArticleUseCaseInteractor(
    private val userService: UserService,
    private val articleRepository: ArticleRepository,
    private val transactionHandler: BlogTransactionHandler,
) : ArticleUseCasesFacade {
    override fun handle(command: IStartToWriteNewDraftArticle.Command): IStartToWriteNewDraftArticle.Info {
        command.validate()
        val article = command.toArticle()

        transactionHandler.runInRepeatableReadTransaction {
            articleRepository.save(article)
        }

        return IStartToWriteNewDraftArticle.Info(article.id)
    }

    private fun IStartToWriteNewDraftArticle.Command.validate() {
        require(userService.isExistUser(this.authorId)) { "유저가 존재하지 않습니다" }
    }

    override fun handle(command: IPublishArticle.Command): IPublishArticle.Info {
        val article = getArticle(command.articleId)

        transactionHandler.runInRepeatableReadTransaction {
            article.publish()
            articleRepository.save(article)
        }

        return IPublishArticle.Info(article.id)
    }

    private fun IStartToWriteNewDraftArticle.Command.toArticle(): Article {
        return Article.newOne(this.authorId)
    }

    override fun handle(command: IDeleteArticle.Command): IDeleteArticle.Info {
        val article = getArticle(command.articleId)

        transactionHandler.runInRepeatableReadTransaction {
            article.delete()
            articleRepository.save(article)
        }

        return IDeleteArticle.Info(article.id)
    }

    override fun handle(command: IUnDeleteArticle.Command): IUnDeleteArticle.Info {
        val article = getArticle(command.articleId)
        require(article.status == Article.Status.DELETED) { "삭제되지 않은 게시글입니다" }

        transactionHandler.runInRepeatableReadTransaction {
            article.unDelete()
            articleRepository.save(article)
        }

        return IUnDeleteArticle.Info(article.id)
    }

    private fun getArticle(articleId: ArticleId) =
        (
            articleRepository.findById(articleId)
                ?: throw IllegalArgumentException("게시글이 존재하지 않습니다")
            )
}
