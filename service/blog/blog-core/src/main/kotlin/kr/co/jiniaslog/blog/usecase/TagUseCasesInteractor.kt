package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.tag.dto.TagDto
import kr.co.jiniaslog.blog.outbound.BlogTransactionHandler
import kr.co.jiniaslog.blog.outbound.TagRepository
import kr.co.jiniaslog.blog.usecase.tag.IDeleteUnUsedTags
import kr.co.jiniaslog.blog.usecase.tag.IGetTopNTags
import kr.co.jiniaslog.blog.usecase.tag.TagUseCasesFacade
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

private val log = mu.KotlinLogging.logger {}

@UseCaseInteractor
class TagUseCasesInteractor(
    private val tagRepository: TagRepository,
    private val transactionHandler: BlogTransactionHandler,
) : TagUseCasesFacade {
    override fun handle(command: IDeleteUnUsedTags.Command): IDeleteUnUsedTags.Info {
        return transactionHandler.runInRepeatableReadTransaction {
            val unUsedTags = tagRepository.findUnUsedTags()
            log.info { "미사용 태그 삭제: ${unUsedTags.map { it.entityId }} " }
            unUsedTags.forEach { tagRepository.deleteById(it.entityId) }
            IDeleteUnUsedTags.Info(unUsedTags.map { TagDto(it.entityId, it.tagName) })
        }
    }

    override fun handle(query: IGetTopNTags.Query): IGetTopNTags.Info {
        return transactionHandler.runInRepeatableReadTransaction {
            val tags = tagRepository.findTopNTags(query.n)
            IGetTopNTags.Info(tags.associate { it.entityId to it.tagName })
        }
    }
}
