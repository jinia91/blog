package kr.co.jiniaslog.tag.usecase

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.jiniaslog.fakes.FakeTagRepository
import kr.co.jiniaslog.memo.domain.tag.TagName
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.memo.usecase.ICreateNewTag
import kr.co.jiniaslog.memo.usecase.impl.TagUseCases
import kr.co.jiniaslog.memo.usecase.impl.TagUseCasesFacade
import kr.co.jiniaslog.shared.CustomBehaviorSpec

internal class TagUseCaseTests : CustomBehaviorSpec() {
    private var tagRepository: TagRepository = FakeTagRepository()
    private val sut: TagUseCasesFacade = TagUseCases(tagRepository = tagRepository)

    init {
        Given("유효한 태그 생성 커맨드가 주어지고") {
            val command = ICreateNewTag.Command(name = TagName("test tag name"))
            When("태그 생성을 요청하면") {
                val result = sut.handle(command)
                Then("태그가 생성된다") {
                    val foundTag = tagRepository.findById(result.tagId)
                    foundTag shouldNotBe null
                    foundTag!!.name shouldBe command.name
                }
            }
        }
    }
}
