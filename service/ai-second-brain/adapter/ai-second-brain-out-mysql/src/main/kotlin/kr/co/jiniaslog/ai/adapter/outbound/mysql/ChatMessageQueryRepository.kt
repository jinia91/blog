package kr.co.jiniaslog.ai.adapter.outbound.mysql

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.jiniaslog.ai.domain.chat.ChatMessage
import kr.co.jiniaslog.ai.domain.chat.ChatMessageId
import kr.co.jiniaslog.ai.domain.chat.ChatSessionId
import kr.co.jiniaslog.ai.domain.chat.QChatMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class ChatMessageQueryRepository(
    @Qualifier("aiJpaQueryFactory")
    private val queryFactory: JPAQueryFactory,
) {
    private val chatMessage = QChatMessage.chatMessage

    fun findBySessionIdWithCursor(
        sessionId: ChatSessionId,
        cursor: ChatMessageId?,
        size: Int,
    ): List<ChatMessage> {
        val query = queryFactory
            .selectFrom(chatMessage)
            .where(chatMessage.sessionId.eq(sessionId))

        if (cursor != null) {
            val cursorMessage = queryFactory
                .selectFrom(chatMessage)
                .where(chatMessage.entityId.eq(cursor))
                .fetchOne()

            if (cursorMessage != null) {
                query.where(
                    chatMessage.createdAt.gt(cursorMessage.createdAt)
                        .or(
                            chatMessage.createdAt.eq(cursorMessage.createdAt)
                                .and(chatMessage.entityId.value.gt(cursor.value))
                        )
                )
            }
        }

        return query
            .orderBy(chatMessage.createdAt.asc(), chatMessage.entityId.value.asc())
            .limit((size + 1).toLong())
            .fetch()
    }
}
