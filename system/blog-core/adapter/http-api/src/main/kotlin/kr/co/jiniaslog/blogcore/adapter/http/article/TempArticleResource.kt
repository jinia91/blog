package kr.co.jiniaslog.blogcore.adapter.http.article

import kr.co.jiniaslog.blogcore.application.article.usecase.TempArticleUseCases
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TempArticleResource(
    private val tempArticleUseCases: TempArticleUseCases,
) {
    @PostMapping("/articles/temp")
    fun autoSaveTemp(@RequestBody request: TempArticleSaveRequest): ResponseEntity<Nothing> {
        tempArticleUseCases.post(request.toCommand())
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/article/temp")
    fun getTempArticle(): TempArticleGetResponse =
        TempArticleGetResponse.from(tempArticleUseCases.findOne())
}
