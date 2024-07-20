package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.outbound.CategoryRepository
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.outbound.UserService
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.article.IAddAnyTagInArticle
import kr.co.jiniaslog.blog.usecase.article.ICategorizeArticle
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IUpdateArticleContents
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class ArticleUseCaseInteractor(
    private val userService: UserService,
    private val articleRepository: ArticleRepository,
    private val transactionHandler: BlogTransactionHandler,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository
) : ArticleUseCasesFacade {
    override fun handle(command: IStartToWriteNewDraftArticle.Command): IStartToWriteNewDraftArticle.Info {
        command.validate()
        val article = command.toArticle()

        transactionHandler.runInRepeatableReadTransaction {
            articleRepository.save(article)
        }

        return IStartToWriteNewDraftArticle.Info(article.entityId)
    }

    private fun IStartToWriteNewDraftArticle.Command.validate() {
        require(userService.isExistUser(this.authorId)) { "유저가 존재하지 않습니다" }
    }

    override fun handle(command: IPublishArticle.Command): IPublishArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.publish()
            articleRepository.save(article)
        }

        return IPublishArticle.Info(article.entityId)
    }

    private fun IStartToWriteNewDraftArticle.Command.toArticle(): Article {
        return Article.newOne(this.authorId)
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

    override fun handle(command: ICategorizeArticle.Command): ICategorizeArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            val category = categoryRepository.findById(command.categoryId)
                ?: throw IllegalArgumentException("카테고리가 존재하지 않습니다")
            article.categorize(category)
            articleRepository.save(article)
        }

        return ICategorizeArticle.Info(article.entityId)
    }

    override fun handle(command: IUpdateArticleContents.Command): IUpdateArticleContents.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.updateArticleContents(command.articleContents)
            articleRepository.save(article)
        }

        return IUpdateArticleContents.Info(article.entityId)
    }

    override fun handle(command: IAddAnyTagInArticle.Command): IAddAnyTagInArticle.Info {
        val tag = transactionHandler.runInRepeatableReadTransaction {
            tagRepository.findByName(command.tagName)
                ?: tagRepository.save(Tag.newOne(command.tagName))
        }

        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.addTag(tag)
            articleRepository.save(article)
        }

        return IAddAnyTagInArticle.Info(article.entityId)
    }

    private fun getArticle(articleId: ArticleId) = articleRepository.findById(articleId)
        ?: throw IllegalArgumentException("게시글이 존재하지 않습니다")
}
