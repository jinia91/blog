package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.usecase.article.ArticleStatusChangeFacade
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IUnPublishArticle
import org.springframework.stereotype.Component

@Component
class ArticleStatusChangeInteractor(
    private val articleRepository: ArticleRepository,
    private val transactionHandler: BlogTransactionHandler,
) : ArticleStatusChangeFacade {
    override fun determineCommand(
        asIsStatus: Article.Status,
        toBeStatus: Article.Status,
        articleId: ArticleId,
    ): ArticleStatusChangeFacade.Command {
        return when (asIsStatus to toBeStatus) {
            Article.Status.DRAFT to Article.Status.PUBLISHED -> IPublishArticle.Command(articleId)
            Article.Status.PUBLISHED to Article.Status.DRAFT -> IUnPublishArticle.Command(articleId)
            Article.Status.DELETED to Article.Status.DRAFT -> IUnDeleteArticle.Command(articleId)
            Article.Status.DRAFT to Article.Status.DELETED -> IDeleteArticle.Command(articleId)
            else -> throw IllegalArgumentException("해당 상태 전이는 허용되지 않습니다")
        }
    }

    override fun handle(command: ArticleStatusChangeFacade.Command): ArticleStatusChangeFacade.Info {
        return when (command) {
            is IPublishArticle.Command -> handle(command)
            is IUnPublishArticle.Command -> handle(command)
            is IDeleteArticle.Command -> handle(command)
            is IUnDeleteArticle.Command -> handle(command)
            else -> throw IllegalArgumentException("해당 커맨드는 허용되지 않습니다")
        }
    }

    override fun handle(command: IPublishArticle.Command): IPublishArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.publish()
            articleRepository.save(article)
        }

        return IPublishArticle.Info(article.entityId)
    }

    override fun handle(command: IDeleteArticle.Command): IDeleteArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.delete()
            articleRepository.save(article)
        }

        return IDeleteArticle.Info(article.entityId)
    }

    override fun handle(command: IUnDeleteArticle.Command): IUnDeleteArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.unDelete()
            articleRepository.save(article)
        }

        return IUnDeleteArticle.Info(article.entityId)
    }

    override fun handle(command: IUnPublishArticle.Command): IUnPublishArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.unPublish()
            articleRepository.save(article)
        }

        return IUnPublishArticle.Info(article.entityId)
    }

    private fun getArticle(articleId: ArticleId): Article {
        return articleRepository.findById(articleId)
            ?: throw IllegalArgumentException("해당 아티클이 존재하지 않습니다")
    }
}
