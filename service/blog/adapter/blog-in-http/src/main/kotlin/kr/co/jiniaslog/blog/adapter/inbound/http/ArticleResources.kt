package kr.co.jiniaslog.blog.adapter.inbound.http

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.AddTagToArticleRequest
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.AddTagToArticleResponse
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.DeleteArticleResponse
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.GetArticleByIdResponse
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.PublishArticleResponse
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.StartNewArticleResponse
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.UnDeleteArticleResponse
import kr.co.jiniaslog.blog.domain.UserId
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.queries.ArticleQueriesFacade
import kr.co.jiniaslog.blog.queries.IGetArticleById
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.article.IAddAnyTagInArticle
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IPublishArticle
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.co.jiniaslog.blog.usecase.article.IUnDeleteArticle
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = "게시글 API", description = "게시글 생명주기 관련")
class ArticleResources(
    private val articleFacade: ArticleUseCasesFacade,
    private val articleQueryFacade: ArticleQueriesFacade,
) {
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "새로운 게시글 작성 시작",
        description = "게시글 초안 상태로 새로운 게시글 작성을 시작한다.",
        method = "POST",
        security = [SecurityRequirement(name = "access token", scopes = ["ADMIN"])]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "게시글 생성 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = StartNewArticleResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [Content(schema = Schema())]
            ),
            ApiResponse(
                responseCode = "403",
                description = "인가 되지 없음",
                content = [Content(schema = Schema())]
            ),
        ]
    )
    fun startNewArticle(@AuthUserId userId: Long?): ResponseEntity<StartNewArticleResponse> {
        val command = IStartToWriteNewDraftArticle.Command(UserId(userId!!))
        val info = articleFacade.handle(command)
        return ResponseEntity
            .created(URI("/api/v1/articles/${info.articleId.value}"))
            .body(StartNewArticleResponse(info.articleId.value))
    }

    @PutMapping("/{articleId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "게시글 게시", description = "게시글을 공개상태로 게시한다", security = [SecurityRequirement(name = "bearer")])
    fun publishArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<PublishArticleResponse> {
        val command = IPublishArticle.Command(ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(PublishArticleResponse(info.articleId.value))
    }

    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<DeleteArticleResponse> {
        val command = IDeleteArticle.Command(ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(DeleteArticleResponse(info.articleId.value))
    }

    @PutMapping("/{articleId}/undelete")
    @PreAuthorize("hasRole('ADMIN')")
    fun unDeleteArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<UnDeleteArticleResponse> {
        val command = IUnDeleteArticle.Command(ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(UnDeleteArticleResponse(info.articleId.value))
    }

    @PutMapping("/{articleId}/tag")
    @PreAuthorize("hasRole('ADMIN')")
    fun addTagToArticle(
        @PathVariable articleId: Long,
        @RequestBody request: AddTagToArticleRequest,
    ): ResponseEntity<AddTagToArticleResponse> {
        val command = IAddAnyTagInArticle.Command(TagName(request.tagName), ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(AddTagToArticleResponse(info.articleId.value))
    }

    @GetMapping("/{articleId}")
    fun getArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<GetArticleByIdResponse> {
        val info = articleQueryFacade.handle(IGetArticleById.Query(ArticleId(articleId)))
        return ResponseEntity.ok(
            GetArticleByIdResponse(
                id = info.id.value,
                title = info.title,
                content = info.content,
                thumbnailUrl = info.thumbnailUrl,
                tags = info.tags.mapKeys { it.key.id },
                createdAt = info.createdAt,
            )
        )
    }
}
