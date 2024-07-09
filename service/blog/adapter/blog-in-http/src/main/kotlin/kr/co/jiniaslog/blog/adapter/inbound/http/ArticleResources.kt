package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.category.CategoryId
import kr.co.jiniaslog.blog.domain.user.UserId
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.article.ICategorizeArticle
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/articles")
class ArticleResources(private val articleFacade: ArticleUseCasesFacade) {
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    fun startNewArticle(@AuthUserId userId: Long?): ResponseEntity<ArticlePostResponse> {
        val command = IStartToWriteNewDraftArticle.Command(UserId(userId!!))
        val info = articleFacade.handle(command)
        return ResponseEntity
            .created(URI("/api/v1/articles/${info.articleId.value}"))
            .body(ArticlePostResponse(info.articleId.value))
    }

    @PutMapping("/{articleId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    fun publishArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<ArticlePublishResponse> {
        val command = IPublishArticle.Command(ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(ArticlePublishResponse(info.articleId.value))
    }

    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<ArticleDeleteResponse> {
        val command = IDeleteArticle.Command(ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(ArticleDeleteResponse(info.articleId.value))
    }

    @PutMapping("/{articleId}/undelete")
    @PreAuthorize("hasRole('ADMIN')")
    fun unDeleteArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<AunDeleteArticleResponse> {
        val command = IUnDeleteArticle.Command(ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(AunDeleteArticleResponse(info.articleId.value))
    }

    @PutMapping("/{articleId}/category/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun categorizeArticle(
        @PathVariable articleId: Long,
        @PathVariable categoryId: Long,
    ): ResponseEntity<ArticleCategorizeResponse> {
        val command = ICategorizeArticle.Command(ArticleId(articleId), CategoryId(categoryId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(ArticleCategorizeResponse(info.articleId.value))
    }
}
