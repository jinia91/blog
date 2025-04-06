package kr.co.jiniaslog.comment.usecase

import kr.co.jiniaslog.comment.domain.CommentFactory
import kr.co.jiniaslog.comment.outbound.CommentRepository
import kr.co.jiniaslog.comment.outbound.CommentTransactionHandler
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

@UseCaseInteractor
class CommentUseCasesInteractor(
    private val commentFactory: CommentFactory,
    private val commentRepository: CommentRepository,
    private val commentTransactionHandler: CommentTransactionHandler,
) : CommentUseCasesFacade {
    override fun handle(command: ICreateComment.Command): ICreateComment.Info {
        val comment = with(command) {
            commentFactory.create(
                refId = refId,
                refType = refType,
                userId = userId,
                userName = userName,
                password = password,
                parentId = parentId,
                content = content
            )
        }

        return commentTransactionHandler.runInRepeatableReadTransaction {
            commentRepository.save(comment)
        }.let {
            ICreateComment.Info(
                commentId = it.id
            )
        }
    }
}
