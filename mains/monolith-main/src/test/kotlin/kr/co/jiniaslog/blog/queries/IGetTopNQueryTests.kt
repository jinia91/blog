package kr.co.jiniaslog.blog.queries

import jakarta.persistence.EntityManager
import kr.co.jiniaslog.TestContainerAbstractSkeleton
import kr.co.jiniaslog.blog.outbound.ArticleRepository
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.usecase.tag.IGetTopNTags
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class IGetTopNQueryTests : TestContainerAbstractSkeleton() {
    @Autowired
    lateinit var sut: IGetTopNTags

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var em: EntityManager

    @Test
    fun `없는 태그의 이름이 주어지면 게시글에 추가할 수 있다`() {
    }
}
