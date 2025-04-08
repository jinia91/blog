package kr.co.jiniaslog.comment.domain

import kr.co.jiniaslog.comment.outbound.CommentRepository
import kr.co.jiniaslog.comment.outbound.UserService
import kr.co.jiniaslog.shared.core.cypher.PasswordHelper
import org.springframework.stereotype.Component

@Component
class CommentFactory(
    private val userService: UserService,
    private val commentRepository: CommentRepository,
    private val referenceValidator: ReferenceValidator,
) {
    fun create(
        refId: ReferenceId,
        refType: Comment.RefType,
        userId: Long?,
        userName: String?,
        password: String?,
        parentId: CommentId?,
        content: String,
    ): Comment {
        val userInfo = createAuthorInfo(
            userId = userId,
            userName = userName,
            password = password,
        )

        if (!referenceValidator.isValid(refType, refId)) {
            throw IllegalArgumentException("유효하지 않은 참조 타입/ID")
        }

        parentId?.let {
            commentRepository.findById(it) ?: throw IllegalArgumentException("부모 댓글이 존재하지 않음")
        }

        return Comment.newOne(
            refId = refId,
            refType = refType,
            userInfo = userInfo,
            parentId = parentId,
            contents = CommentContents(content),
        )
    }

    private fun createAuthorInfo(
        userId: Long?,
        userName: String?,
        password: String?,
    ): AuthorInfo {
        val isRegisteredUser = userId != null && userName == null && password == null
        return if (isRegisteredUser) {
            userService.getUserInfo(userId!!)
        } else {
            AuthorInfo(
                authorId = null,
                authorName = userName ?: throw IllegalArgumentException("유저 이름은 필수"),
                password = PasswordHelper.encode(
                    password ?: throw IllegalArgumentException("비밀번호는 필수")
                ),
                profileImageUrl = null
            )
        }
    }
}
