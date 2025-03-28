package kr.co.jiniaslog.blog.adapter.inbound.http

import kr.co.jiniaslog.blog.adapter.inbound.http.dto.TagViewModel
import kr.co.jiniaslog.blog.adapter.inbound.http.dto.TopNTagResponse
import kr.co.jiniaslog.blog.usecase.tag.IGetTopNTags
import kr.co.jiniaslog.blog.usecase.tag.TagUseCasesFacade
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tags")
class TagResources(
    private val tagUseCasesFacade: TagUseCasesFacade
) {
    @GetMapping("/top")
    fun getTopNTags(@RequestParam n: Int): ResponseEntity<TopNTagResponse> {
        val tags = tagUseCasesFacade.handle(IGetTopNTags.Query(n)).tags
        return ResponseEntity.ok(TopNTagResponse(tags.map { TagViewModel(it.key.id, it.value.value) }))
    }
}
