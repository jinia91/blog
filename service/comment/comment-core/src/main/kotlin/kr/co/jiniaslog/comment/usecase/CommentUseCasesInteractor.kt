package kr.co.jiniaslog.comment.usecase

import kr.co.jiniaslog.comment.domain.CommentFactory
import kr.co.jiniaslog.comment.outbound.CommentRepository
import kr.co.jiniaslog.comment.outbound.CommentTransactionHandler
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@UseCaseInteractor
class CommentUseCasesInteractor(
    private val commentFactory: CommentFactory,
    private val commentRepository: CommentRepository,
    private val commentTransactionHandler: CommentTransactionHandler,
    private val passwordEncoder: BCryptPasswordEncoder,
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

    override fun handle(command: IDeleteComment.Command): IDeleteComment.Info {
        val comment = commentRepository.findById(command.commentId)
            ?: throw IllegalArgumentException("댓글이 존재하지 않습니다")

        if (comment.authorInfo.isAnonymous()) {
            require(command.password != null) { "비밀번호가 필요합니다" }
            check(passwordEncoder.matches(command.password, comment.authorInfo.password)) { ("비밀번호가 필요합니다") }
        } else {
            require(command.password == null) { "비밀번호는 필요하지 않습니다" }
            require(command.authorId == comment.authorInfo.authorId) { "댓글 작성자만 삭제할 수 있습니다" }
        }

        comment.delete()

        commentTransactionHandler.runInRepeatableReadTransaction {
            commentRepository.save(comment)
        }

        return IDeleteComment.Info(
            commentId = command.commentId
        )
    }
}
