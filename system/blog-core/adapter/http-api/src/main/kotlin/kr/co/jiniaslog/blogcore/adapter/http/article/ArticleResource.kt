package kr.co.jiniaslog.blogcore.adapter.http.article

import kr.co.jiniaslog.blogcore.application.article.usecase.DraftArticlePostUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ArticleResource(
    private val draftArticlePostUseCase: DraftArticlePostUseCase,
) {
    @PostMapping("/articles")
    fun postArticle(@RequestBody request: DraftArticlePostRequest) {
        draftArticlePostUseCase.post(request.toCommand())
    }
}
