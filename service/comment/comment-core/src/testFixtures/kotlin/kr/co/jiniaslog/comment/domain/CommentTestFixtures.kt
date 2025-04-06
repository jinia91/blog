package kr.co.jiniaslog.comment.domain

import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.lang.reflect.Constructor

val constructor: Constructor<Comment> = Comment::class.java.getDeclaredConstructor(
    CommentId::class.java,
    UserInfo::class.java,
    ReferenceId::class.java,
    Comment.Status::class.java,
    CommentContents::class.java
).apply {
    isAccessible = true
}

object CommentTestFixtures {
    fun createNoneUserComment(
        id: CommentId = CommentId(IdUtils.generate()),
        userName: String,
        userPassword: String,
        refId: ReferenceId = ReferenceId(IdUtils.generate()),
        status: Comment.Status = Comment.Status.ACTIVE,
        contents: CommentContents = CommentContents("contents"),
    ): Comment {
        return constructor.newInstance(
            id,
            UserInfo(
                userId = null,
                userName = userName,
                password = userPassword
            ),
            refId,
            status,
            contents
        )
    }

    fun createUserComment(
        id: CommentId = CommentId(IdUtils.generate()),
        userId: Long,
        userName: String,
        userPassword: String,
        refId: ReferenceId = ReferenceId(IdUtils.generate()),
        status: Comment.Status = Comment.Status.ACTIVE,
        contents: CommentContents = CommentContents("contents"),
    ): Comment {
        return constructor.newInstance(
            id,
            UserInfo(
                userId = userId,
                userName = userName,
                password = userPassword
            ),
            refId,
            status,
            contents
        )
    }
}
