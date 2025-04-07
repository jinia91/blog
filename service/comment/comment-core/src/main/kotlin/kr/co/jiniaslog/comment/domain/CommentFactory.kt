package kr.co.jiniaslog.comment.domain

import kr.co.jiniaslog.comment.outbound.CommentRepository
import kr.co.jiniaslog.comment.outbound.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class CommentFactory(
    private val userService: UserService,
    private val commentRepository: CommentRepository,
    private val referenceValidator: ReferenceValidator,
    private val passwordEncoder: BCryptPasswordEncoder,
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
        val userInfo = createUserInfo(
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

    private fun createUserInfo(
        userId: Long?,
        userName: String?,
        password: String?,
    ): UserInfo {
        return if (userId != null && userName == null && password == null) {
            userService.getUserInfo(userId)
        } else {
            UserInfo(
                authorId = null,
                authorName = userName ?: throw IllegalArgumentException("유저 이름은 필수"),
                password = passwordEncoder.encode(
                    password ?: throw IllegalArgumentException("비밀번호는 필수")
                )
            )
        }
    }
}
