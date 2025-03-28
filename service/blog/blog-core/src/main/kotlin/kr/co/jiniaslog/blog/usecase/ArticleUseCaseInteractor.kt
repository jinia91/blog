package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.outbound.UserService
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.article.IAddAnyTagInArticle
import kr.co.jiniaslog.blog.usecase.article.IRemoveTagInArticle
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.co.jiniaslog.blog.usecase.article.IUpdateDraftArticleContents
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class ArticleUseCaseInteractor(
    private val userService: UserService,
    private val articleRepository: ArticleRepository,
    private val transactionHandler: BlogTransactionHandler,
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

    private fun IStartToWriteNewDraftArticle.Command.toArticle(): Article {
        return Article.newOne(this.authorId)
    }

    override fun handle(command: IUpdateDraftArticleContents.Command): IUpdateDraftArticleContents.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val article = getArticle(command.articleId)
            article.updateDraftArticleContents(command.articleContents)
            articleRepository.save(article)
        }

        return IUpdateDraftArticleContents.Info(article.entityId)
    }

    override fun handle(command: IAddAnyTagInArticle.Command): IAddAnyTagInArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val tag = tagRepository.findByName(command.tagName)
                ?: tagRepository.save(Tag.newOne(command.tagName))
            val article = getArticle(command.articleId)
            article.addTag(tag)
            articleRepository.save(article)
        }

        return IAddAnyTagInArticle.Info(article.entityId)
    }

    override fun handle(command: IRemoveTagInArticle.Command): IRemoveTagInArticle.Info {
        val article = transactionHandler.runInRepeatableReadTransaction {
            val tag = tagRepository.findByName(command.tagName)
                ?: throw IllegalArgumentException("태그가 존재하지 않습니다")
            val article = getArticle(command.articleId)
            article.removeTag(tag)
            articleRepository.save(article)
        }

        return IRemoveTagInArticle.Info(article.entityId)
    }

    private fun getArticle(articleId: ArticleId) = articleRepository.findById(articleId)
        ?: throw IllegalArgumentException("게시글이 존재하지 않습니다")
}
