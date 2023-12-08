package kr.co.jiniaslog.memo.usecase.impl

import kr.co.jiniaslog.memo.domain.tag.Tag
import kr.co.jiniaslog.memo.domain.tag.TagRepository
import kr.co.jiniaslog.memo.usecase.ICreateNewTag
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface TagUseCasesFacade :
    ICreateNewTag

@UseCaseInteractor
internal class TagUseCases(
    private val tagRepository: TagRepository,
) : TagUseCasesFacade {
    override fun handle(command: ICreateNewTag.Command): ICreateNewTag.Info {
        val tag = tagRepository.save(Tag.init(command.name))
        return ICreateNewTag.Info(tag.id)
    }
}
