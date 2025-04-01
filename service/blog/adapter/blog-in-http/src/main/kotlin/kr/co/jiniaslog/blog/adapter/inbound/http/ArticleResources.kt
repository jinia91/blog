package kr.co.jiniaslog.blog.adapter.inbound.http

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.SimpleArticleCardsViewModel
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.StartNewArticleResponse
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.TagViewModel
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.UpdateArticleStatusRequest
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.UpdateArticleStatusResponse
import kr.co.jiniaslog.blog.domain.UserId
import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.Article.Status.DRAFT
import kr.co.jiniaslog.blog.domain.article.Article.Status.PUBLISHED
import kr.co.jiniaslog.blog.domain.article.ArticleId
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.queries.ArticleQueriesFacade
import kr.co.jiniaslog.blog.queries.IGetExpectedStatusArticleById
import kr.co.jiniaslog.blog.queries.IGetSimpleArticles
import kr.co.jiniaslog.blog.usecase.article.ArticleStatusChangeFacade
import kr.co.jiniaslog.blog.usecase.article.ArticleUseCasesFacade
import kr.co.jiniaslog.blog.usecase.article.IAddAnyTagInArticle
import kr.co.jiniaslog.blog.usecase.article.IDeleteArticle
import kr.co.jiniaslog.blog.usecase.article.IRemoveTagInArticle
import kr.co.jiniaslog.blog.usecase.article.IStartToWriteNewDraftArticle
import kr.cojiniaslog.shared.adapter.inbound.http.AuthUserId
import kr.cojiniaslog.shared.adapter.inbound.http.CommonApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = "게시글 API", description = "게시글 생명주기 관련")
@CommonApiResponses
class ArticleResources(
    private val articleFacade: ArticleUseCasesFacade,
    private val articleStatusChangeFacade: ArticleStatusChangeFacade,
    private val articleQueryFacade: ArticleQueriesFacade,
) {
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "새로운 게시글 작성 시작",
        description = "게시글 초안 상태로 새로운 게시글 작성을 시작한다."
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
            )
        ]
    )
    fun startNewArticle(@AuthUserId userId: Long?): ResponseEntity<StartNewArticleResponse> {
        val command = IStartToWriteNewDraftArticle.Command(UserId(userId!!))
        val info = articleFacade.handle(command)
        return ResponseEntity
            .created(URI("/api/v1/articles/${info.articleId.value}"))
            .body(StartNewArticleResponse(info.articleId.value))
    }

    @DeleteMapping("/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "게시글 삭제", description = "게시글을 논리 삭제한다, 복원 가능")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "게시글 삭제 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = DeleteArticleResponse::class)
                    )
                ]
            )
        ]
    )
    fun deleteArticle(
        @PathVariable articleId: Long,
    ): ResponseEntity<DeleteArticleResponse> {
        val command = IDeleteArticle.Command(ArticleId(articleId))
        val info = articleStatusChangeFacade.handle(command)
        return ResponseEntity.ok(DeleteArticleResponse(info.articleId.value))
    }

    @PatchMapping("/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "게시글 상태 변경",
        description = "게시글을 게시하거나 내리거나 삭제를 복구한다.",
        security = [SecurityRequirement(name = "bearer")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "게시글 상태 변경 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UpdateArticleStatusResponse::class)
                    )
                ]
            )
        ]
    )
    fun updateArticleStatus(
        @PathVariable articleId: Long,
        @RequestBody request: UpdateArticleStatusRequest
    ): ResponseEntity<UpdateArticleStatusResponse> {
        val command = articleStatusChangeFacade.determineCommand(
            request.asIsStatus,
            request.toBeStatus,
            ArticleId(articleId)
        )
        val info = articleStatusChangeFacade.handle(command)
        return ResponseEntity.ok(UpdateArticleStatusResponse(info.articleId.value))
    }

    @PostMapping("/{articleId}/tags")
    @PreAuthorize("hasRole('ADMIN')")
    fun addTagToArticle(
        @PathVariable articleId: Long,
        @RequestBody request: AddTagToArticleRequest,
    ): ResponseEntity<AddTagToArticleResponse> {
        val command = IAddAnyTagInArticle.Command(TagName(request.tagName), ArticleId(articleId))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(AddTagToArticleResponse(info.articleId.value))
    }

    @DeleteMapping("/{articleId}/tags/{tagName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeTagFromArticle(
        @PathVariable articleId: Long,
        @PathVariable tagName: String,
    ): ResponseEntity<DeleteTaggingResponse> {
        val command = IRemoveTagInArticle.Command(ArticleId(articleId), TagName(tagName))
        val info = articleFacade.handle(command)
        return ResponseEntity.ok(DeleteTaggingResponse(info.articleId.value))
    }

    @GetMapping("/{articleId}")
    @PreAuthorize("!(#expectedStatus.name() == 'DRAFT') or hasRole('ADMIN')")
    fun getArticle(
        @PathVariable articleId: Long,
        @RequestParam expectedStatus: Article.Status,
    ): ResponseEntity<GetArticleByIdResponse> {
        val info = articleQueryFacade.handle(IGetExpectedStatusArticleById.Query(ArticleId(articleId), expectedStatus))
        return ResponseEntity.ok(
            GetArticleByIdResponse(
                id = info.id.value,
                title = info.title,
                content = info.content,
                thumbnailUrl = info.thumbnailUrl,
                tags = info.tags.map { TagViewModel(it.key.id, it.value) },
                createdAt = info.createdAt,
                isPublished = info.isPublished
            )
        )
    }

    @GetMapping("/simple")
    @PreAuthorize("!(#status.name() == 'DRAFT') or hasRole('ADMIN')")
    @Operation(
        summary = "게시글 카드 목록 조회",
        description = "게시글의 간단한 정보를 조회한다. 키워드 검색은 커서와 함께 사용할 수 없다. 태그 검색은 커서와 함께 사용할 수 없다."
    )
    fun getSimpleArticleCards(
        @Parameter(description = "조회하려는 게시글 상태", required = true)
        @RequestParam(required = true)
        status: Article.Status?,

        @Parameter(description = "페이징을 위한 커서, 가장 마지막 글의 Id", required = false)
        @RequestParam(required = false)
        cursor: Long?,

        @Parameter(description = "조회할 게시글 수", required = false)
        @RequestParam(required = false)
        limit: Int?,

        @Parameter(description = "검색 키워드", required = false)
        @RequestParam(required = false)
        keyword: String?,

        @Parameter(description = "태그 이름", required = false)
        @RequestParam(required = false)
        tagName: String?,
    ): ResponseEntity<List<SimpleArticleCardsViewModel>> {
        val isPublished = when (status) {
            DRAFT -> false
            PUBLISHED -> true
            else -> throw IllegalArgumentException("status는 DRAFT 또는 PUBLISHED만 가능합니다")
        }
        val info = articleQueryFacade.handle(IGetSimpleArticles.Query(cursor, limit, isPublished, keyword, tagName))
        return ResponseEntity.ok(
            info.articles.map {
                SimpleArticleCardsViewModel(
                    id = it.id,
                    title = it.title,
                    thumbnailUrl = it.thumbnailUrl,
                    createdAt = it.createdAt,
                    tags = it.tags.map { tag -> TagViewModel(tag.key, tag.value) },
                    content = it.content,
                    contentStatus = status
                )
            }
        )
    }
}
