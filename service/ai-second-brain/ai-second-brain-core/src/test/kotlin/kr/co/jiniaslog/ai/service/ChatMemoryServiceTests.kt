package kr.co.jiniaslog.ai.service

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage

class ChatMemoryServiceTests : SimpleUnitTestContext() {

    private lateinit var chatMemoryService: ChatMemoryService

    @BeforeEach
    fun setUp() {
        chatMemoryService = ChatMemoryService()
    }

    @Nested
    inner class `addUserMessage 테스트` {
        @Test
        fun `유저 메시지를 대화에 추가할 수 있다`() {
            // given
            val conversationId = "conv-1"
            val content = "안녕하세요"

            // when
            chatMemoryService.addUserMessage(conversationId, content)

            // then
            val messages = chatMemoryService.getMessages(conversationId)
            messages shouldHaveSize 1
            messages[0] shouldBe UserMessage(content)
        }

        @Test
        fun `새로운 conversationId는 새 리스트를 생성한다`() {
            // given
            val conversationId1 = "conv-1"
            val conversationId2 = "conv-2"

            // when
            chatMemoryService.addUserMessage(conversationId1, "첫 번째 대화")
            chatMemoryService.addUserMessage(conversationId2, "두 번째 대화")

            // then
            val messages1 = chatMemoryService.getMessages(conversationId1)
            val messages2 = chatMemoryService.getMessages(conversationId2)

            messages1 shouldHaveSize 1
            messages2 shouldHaveSize 1
            (messages1[0] as UserMessage).text shouldBe "첫 번째 대화"
            (messages2[0] as UserMessage).text shouldBe "두 번째 대화"
        }

        @Test
        fun `동일한 conversationId에 여러 메시지를 추가할 수 있다`() {
            // given
            val conversationId = "conv-1"

            // when
            chatMemoryService.addUserMessage(conversationId, "메시지 1")
            chatMemoryService.addUserMessage(conversationId, "메시지 2")
            chatMemoryService.addUserMessage(conversationId, "메시지 3")

            // then
            val messages = chatMemoryService.getMessages(conversationId)
            messages shouldHaveSize 3
            (messages[0] as UserMessage).text shouldBe "메시지 1"
            (messages[1] as UserMessage).text shouldBe "메시지 2"
            (messages[2] as UserMessage).text shouldBe "메시지 3"
        }
    }

    @Nested
    inner class `addAssistantMessage 테스트` {
        @Test
        fun `어시스턴트 메시지를 대화에 추가할 수 있다`() {
            // given
            val conversationId = "conv-1"
            val content = "무엇을 도와드릴까요?"

            // when
            chatMemoryService.addAssistantMessage(conversationId, content)

            // then
            val messages = chatMemoryService.getMessages(conversationId)
            messages shouldHaveSize 1
            messages[0] shouldBe AssistantMessage(content)
        }

        @Test
        fun `새로운 conversationId는 새 리스트를 생성한다`() {
            // given
            val conversationId1 = "conv-1"
            val conversationId2 = "conv-2"

            // when
            chatMemoryService.addAssistantMessage(conversationId1, "응답 1")
            chatMemoryService.addAssistantMessage(conversationId2, "응답 2")

            // then
            val messages1 = chatMemoryService.getMessages(conversationId1)
            val messages2 = chatMemoryService.getMessages(conversationId2)

            messages1 shouldHaveSize 1
            messages2 shouldHaveSize 1
            (messages1[0] as AssistantMessage).text shouldBe "응답 1"
            (messages2[0] as AssistantMessage).text shouldBe "응답 2"
        }

        @Test
        fun `유저 메시지와 어시스턴트 메시지를 섞어서 추가할 수 있다`() {
            // given
            val conversationId = "conv-1"

            // when
            chatMemoryService.addUserMessage(conversationId, "질문 1")
            chatMemoryService.addAssistantMessage(conversationId, "답변 1")
            chatMemoryService.addUserMessage(conversationId, "질문 2")
            chatMemoryService.addAssistantMessage(conversationId, "답변 2")

            // then
            val messages = chatMemoryService.getMessages(conversationId)
            messages shouldHaveSize 4
            messages[0] shouldBe UserMessage("질문 1")
            messages[1] shouldBe AssistantMessage("답변 1")
            messages[2] shouldBe UserMessage("질문 2")
            messages[3] shouldBe AssistantMessage("답변 2")
        }
    }

    @Nested
    inner class `getMessages 테스트` {
        @Test
        fun `존재하는 대화의 메시지를 반환한다`() {
            // given
            val conversationId = "conv-1"
            chatMemoryService.addUserMessage(conversationId, "안녕")
            chatMemoryService.addAssistantMessage(conversationId, "반갑습니다")

            // when
            val messages = chatMemoryService.getMessages(conversationId)

            // then
            messages shouldHaveSize 2
            (messages[0] as UserMessage).text shouldBe "안녕"
            (messages[1] as AssistantMessage).text shouldBe "반갑습니다"
        }

        @Test
        fun `존재하지 않는 대화의 경우 빈 리스트를 반환한다`() {
            // given
            val nonExistentConversationId = "non-existent"

            // when
            val messages = chatMemoryService.getMessages(nonExistentConversationId)

            // then
            messages.shouldBeEmpty()
        }

        @Test
        fun `반환된 리스트는 원본과 독립적이다`() {
            // given
            val conversationId = "conv-1"
            chatMemoryService.addUserMessage(conversationId, "메시지 1")

            // when
            val messages1 = chatMemoryService.getMessages(conversationId)
            chatMemoryService.addUserMessage(conversationId, "메시지 2")
            val messages2 = chatMemoryService.getMessages(conversationId)

            // then
            messages1 shouldHaveSize 1
            messages2 shouldHaveSize 2
        }
    }

    @Nested
    inner class `clear 테스트` {
        @Test
        fun `특정 대화의 히스토리를 제거한다`() {
            // given
            val conversationId = "conv-1"
            chatMemoryService.addUserMessage(conversationId, "메시지")
            chatMemoryService.addAssistantMessage(conversationId, "응답")

            // when
            chatMemoryService.clear(conversationId)

            // then
            val messages = chatMemoryService.getMessages(conversationId)
            messages.shouldBeEmpty()
        }

        @Test
        fun `다른 대화의 히스토리는 영향을 받지 않는다`() {
            // given
            val conversationId1 = "conv-1"
            val conversationId2 = "conv-2"
            chatMemoryService.addUserMessage(conversationId1, "대화 1")
            chatMemoryService.addUserMessage(conversationId2, "대화 2")

            // when
            chatMemoryService.clear(conversationId1)

            // then
            val messages1 = chatMemoryService.getMessages(conversationId1)
            val messages2 = chatMemoryService.getMessages(conversationId2)

            messages1.shouldBeEmpty()
            messages2 shouldHaveSize 1
        }

        @Test
        fun `존재하지 않는 대화를 clear해도 예외가 발생하지 않는다`() {
            // given
            val nonExistentConversationId = "non-existent"

            // when & then (예외가 발생하지 않아야 함)
            chatMemoryService.clear(nonExistentConversationId)
        }
    }

    @Nested
    inner class `clearAll 테스트` {
        @Test
        fun `모든 대화 히스토리를 제거한다`() {
            // given
            chatMemoryService.addUserMessage("conv-1", "메시지 1")
            chatMemoryService.addUserMessage("conv-2", "메시지 2")
            chatMemoryService.addUserMessage("conv-3", "메시지 3")

            // when
            chatMemoryService.clearAll()

            // then
            chatMemoryService.getMessages("conv-1").shouldBeEmpty()
            chatMemoryService.getMessages("conv-2").shouldBeEmpty()
            chatMemoryService.getMessages("conv-3").shouldBeEmpty()
        }

        @Test
        fun `clearAll 후에도 새 메시지를 추가할 수 있다`() {
            // given
            chatMemoryService.addUserMessage("conv-1", "이전 메시지")
            chatMemoryService.clearAll()

            // when
            chatMemoryService.addUserMessage("conv-1", "새 메시지")

            // then
            val messages = chatMemoryService.getMessages("conv-1")
            messages shouldHaveSize 1
            (messages[0] as UserMessage).text shouldBe "새 메시지"
        }
    }

    @Nested
    inner class `trimMessages 테스트` {
        @Test
        fun `최대 개수를 초과하면 가장 오래된 메시지부터 제거된다`() {
            // given
            val conversationId = "conv-1"

            // 21개의 메시지 추가 (DEFAULT_MAX_MESSAGES = 20)
            repeat(21) { index ->
                chatMemoryService.addUserMessage(conversationId, "메시지 ${index + 1}")
            }

            // when
            val messages = chatMemoryService.getMessages(conversationId)

            // then
            messages shouldHaveSize 20
            (messages[0] as UserMessage).text shouldBe "메시지 2" // 첫 번째 메시지가 제거됨
            (messages[19] as UserMessage).text shouldBe "메시지 21"
        }

        @Test
        fun `최대 개수 미만일 때는 메시지가 제거되지 않는다`() {
            // given
            val conversationId = "conv-1"

            // 10개의 메시지 추가
            repeat(10) { index ->
                chatMemoryService.addUserMessage(conversationId, "메시지 ${index + 1}")
            }

            // when
            val messages = chatMemoryService.getMessages(conversationId)

            // then
            messages shouldHaveSize 10
            (messages[0] as UserMessage).text shouldBe "메시지 1"
            (messages[9] as UserMessage).text shouldBe "메시지 10"
        }

        @Test
        fun `최대 개수와 정확히 같을 때는 메시지가 제거되지 않는다`() {
            // given
            val conversationId = "conv-1"

            // 정확히 20개의 메시지 추가
            repeat(20) { index ->
                chatMemoryService.addUserMessage(conversationId, "메시지 ${index + 1}")
            }

            // when
            val messages = chatMemoryService.getMessages(conversationId)

            // then
            messages shouldHaveSize 20
            (messages[0] as UserMessage).text shouldBe "메시지 1"
            (messages[19] as UserMessage).text shouldBe "메시지 20"
        }

        @Test
        fun `존재하지 않는 대화에 대해서는 아무 일도 일어나지 않는다`() {
            // given
            val nonExistentConversationId = "non-existent"

            // when (addUserMessage가 내부적으로 trimMessages를 호출하지 않는 시나리오)
            // trimMessages는 private이므로 간접적으로 테스트

            // then
            val messages = chatMemoryService.getMessages(nonExistentConversationId)
            messages.shouldBeEmpty()
        }

        @Test
        fun `여러 번 최대 개수를 초과해도 항상 20개만 유지된다`() {
            // given
            val conversationId = "conv-1"

            // 25개의 메시지를 추가
            repeat(25) { index ->
                chatMemoryService.addUserMessage(conversationId, "메시지 ${index + 1}")
            }

            // when
            val messages = chatMemoryService.getMessages(conversationId)

            // then
            messages shouldHaveSize 20
            (messages[0] as UserMessage).text shouldBe "메시지 6" // 처음 5개가 제거됨
            (messages[19] as UserMessage).text shouldBe "메시지 25"
        }

        @Test
        fun `대화 추가 시마다 trim이 자동으로 실행된다`() {
            // given
            val conversationId = "conv-1"

            // 먼저 19개 추가
            repeat(19) { index ->
                chatMemoryService.addUserMessage(conversationId, "메시지 ${index + 1}")
            }

            // when
            chatMemoryService.addUserMessage(conversationId, "메시지 20") // 20개째
            chatMemoryService.addUserMessage(conversationId, "메시지 21") // 21개째, trim 발생

            // then
            val messages = chatMemoryService.getMessages(conversationId)
            messages shouldHaveSize 20
            (messages[0] as UserMessage).text shouldBe "메시지 2" // 첫 번째 메시지 제거됨
            (messages[19] as UserMessage).text shouldBe "메시지 21"
        }
    }

    @Nested
    inner class `동시성 테스트` {
        @Test
        fun `서로 다른 대화는 독립적으로 관리된다`() {
            // given
            val conversationId1 = "conv-1"
            val conversationId2 = "conv-2"
            val conversationId3 = "conv-3"

            // when
            chatMemoryService.addUserMessage(conversationId1, "대화1-메시지1")
            chatMemoryService.addUserMessage(conversationId2, "대화2-메시지1")
            chatMemoryService.addUserMessage(conversationId1, "대화1-메시지2")
            chatMemoryService.addAssistantMessage(conversationId2, "대화2-응답1")
            chatMemoryService.addUserMessage(conversationId3, "대화3-메시지1")

            // then
            val messages1 = chatMemoryService.getMessages(conversationId1)
            val messages2 = chatMemoryService.getMessages(conversationId2)
            val messages3 = chatMemoryService.getMessages(conversationId3)

            messages1 shouldHaveSize 2
            messages2 shouldHaveSize 2
            messages3 shouldHaveSize 1
        }
    }
}
