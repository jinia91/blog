package kr.co.jiniaslog.comment.domain

import kr.co.jiniaslog.shared.core.cypher.PasswordHelper
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.lang.reflect.Constructor

val constructor: Constructor<Comment> = Comment::class.java.getDeclaredConstructor(
    CommentId::class.java,
    AuthorInfo::class.java,
    ReferenceId::class.java,
    Comment.RefType::class.java,
    CommentId::class.java,
    Comment.Status::class.java,
    CommentContents::class.java
).apply {
    isAccessible = true
}

object CommentTestFixtures {
    fun createAnonymousComment(
        id: CommentId = CommentId(IdUtils.generate()),
        userName: String = "userName",
        userPassword: String = "userPassword",
        refId: ReferenceId = ReferenceId(IdUtils.generate()),
        refType: Comment.RefType = Comment.RefType.ARTICLE,
        parentId: CommentId? = null,
        status: Comment.Status = Comment.Status.ACTIVE,
        contents: CommentContents = CommentContents("contents"),
    ): Comment {
        return constructor.newInstance(
            id,
            AuthorInfo(
                authorId = null,
                authorName = userName,
                password = PasswordHelper.encode(userPassword),
                profileImageUrl = null
            ),
            refId,
            refType,
            parentId,
            status,
            contents
        )
    }

    fun createUserComment(
        id: CommentId = CommentId(IdUtils.generate()),
        userId: Long,
        userName: String,
        refId: ReferenceId = ReferenceId(IdUtils.generate()),
        refType: Comment.RefType = Comment.RefType.ARTICLE,
        parentId: CommentId? = null,
        status: Comment.Status = Comment.Status.ACTIVE,
        contents: CommentContents = CommentContents("contents"),
    ): Comment {
        return constructor.newInstance(
            id,
            AuthorInfo(
                authorId = userId,
                authorName = userName,
                password = null,
                profileImageUrl = null
            ),
            refId,
            refType,
            parentId,
            status,
            contents
        )
    }
}
