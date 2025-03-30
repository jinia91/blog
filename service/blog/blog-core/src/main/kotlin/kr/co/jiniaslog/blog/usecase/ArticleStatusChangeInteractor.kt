package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.Article.Status.DELETED
import kr.co.jiniaslog.blog.domain.article.Article.Status.DRAFT
import kr.co.jiniaslog.blog.domain.article.Article.Status.PUBLISHED
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.usecase.article.ArticleStatusChangeFacade
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IUnPublishArticle
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class ArticleStatusChangeInteractor(
    private val articleRepository: ArticleRepository,
    private val transactionHandler: BlogTransactionHandler,
) : ArticleStatusChangeFacade {

    @CacheEvict(cacheNames = ["blog.article.simple"], allEntries = true)
    override fun determineCommand(
        asIsStatus: Article.Status,
        toBeStatus: Article.Status,
        articleId: ArticleId,
    ): ArticleStatusChangeFacade.Command {
        return when (asIsStatus to toBeStatus) {
            DRAFT to PUBLISHED,
            PUBLISHED to PUBLISHED -> IPublishArticle.Command(articleId)
            PUBLISHED to DRAFT -> IUnPublishArticle.Command(articleId)
            DELETED to DRAFT -> IUnDeleteArticle.Command(articleId)
            PUBLISHED to DELETED,
            DRAFT to DELETED -> IDeleteArticle.Command(articleId)
            else -> throw IllegalArgumentException("해당 상태 전이는 허용되지 않습니다")
        }
    }

    override fun handle(command: ArticleStatusChangeFacade.Command): ArticleStatusChangeFacade.Info {
        return when (command) { // 타입캐스팅후 오버로딩
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
