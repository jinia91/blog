package kr.co.jiniaslog.ai.adapter.outbound.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.ai.domain.chat.AuthorId
import kr.co.jiniaslog.ai.domain.chat.ChatSession
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.QChatSession
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class ChatSessionQueryRepository(
    @Qualifier("aiJpaQueryFactory")
    private val queryFactory: JPAQueryFactory,
) {
    private val chatSession = QChatSession.chatSession

    fun findByAuthorIdWithCursor(
        authorId: AuthorId,
        cursor: ChatSessionId?,
        size: Int,
    ): List<ChatSession> {
        val query = queryFactory
            .selectFrom(chatSession)
            .where(chatSession.authorId.eq(authorId))

        if (cursor != null) {
            val cursorSession = queryFactory
                .selectFrom(chatSession)
                .where(chatSession.entityId.eq(cursor))
                .fetchOne()

            if (cursorSession != null) {
                val cursorUpdatedAt = cursorSession.updatedAt ?: return emptyList()
                query.where(
                    chatSession.updatedAt.lt(cursorUpdatedAt)
                        .or(
                            chatSession.updatedAt.eq(cursorUpdatedAt)
                                .and(chatSession.entityId.value.lt(cursor.value))
                        )
                )
            }
        }

        return query
            .orderBy(chatSession.updatedAt.desc(), chatSession.entityId.value.desc())
            .limit(size.toLong())
            .fetch()
    }
}
