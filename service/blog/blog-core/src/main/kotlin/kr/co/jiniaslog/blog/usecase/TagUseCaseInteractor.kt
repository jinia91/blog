package kr.co.jiniaslog.blog.usecase

import kr.co.jiniaslog.blog.domain.tag.Tag
import kr.co.jiniaslog.blog.outbound.persistence.TagRepository
import kr.co.jiniaslog.blog.usecase.tag.ICreateNewTagIfNotExist
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class TagUseCaseInteractor(
    private val tagRepository: TagRepository
) : ICreateNewTagIfNotExist {
    override fun handle(command: ICreateNewTagIfNotExist.Command): ICreateNewTagIfNotExist.Info {
        val tag = tagRepository.findByName(command.name)
        return if (tag == null) {
            val newTag = tagRepository.save(Tag.newOne(command.name))
            ICreateNewTagIfNotExist.Info(newTag.entityId)
        } else {
            ICreateNewTagIfNotExist.Info(tag.entityId)
        }
    }
}
