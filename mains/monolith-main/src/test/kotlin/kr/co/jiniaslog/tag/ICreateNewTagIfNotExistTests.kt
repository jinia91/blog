package kr.co.jiniaslog.tag

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.domain.tag.TagName
import kr.co.jiniaslog.blog.outbound.persistence.TagRepository
import kr.co.jiniaslog.blog.usecase.tag.ICreateNewTagIfNotExist
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ICreateNewTagIfNotExistTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: ICreateNewTagIfNotExist

    @Autowired
    lateinit var tagRepository: TagRepository

    @Test
    fun `없는 태그 생성 요청이 있으면 태그가 생성된다`() {
        // given
        val command = ICreateNewTagIfNotExist.Command(TagName("tag"))

        // when
        val result = sut.handle(command)

        // then
        result.tagId.shouldNotBeNull()
    }

    @Test
    fun `존재하는 태그 이름의 생성 요청이 있을경우 기존 태그의 아이디를 반환한다`() {
        // given
        val tagId = tagRepository.save(Tag.newOne(TagName("tag"))).entityId

        val command = ICreateNewTagIfNotExist.Command(TagName("tag"))

        // when
        val result = sut.handle(command)

        // then
        result.tagId.shouldNotBeNull()
        result.tagId.shouldBe(tagId)
    }
}
