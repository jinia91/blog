package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient

class IntentRouterAgentTests : SimpleUnitTestContext() {

    private fun createMockChatClient(response: String?): ChatClient {
        val chatClient = mockk<ChatClient>(relaxed = true)
        val promptSpec = mockk<ChatClient.ChatClientRequestSpec>(relaxed = true)
        val callResponseSpec = mockk<ChatClient.CallResponseSpec>(relaxed = true)

        every { chatClient.prompt() } returns promptSpec
        every { promptSpec.user(any<String>()) } returns promptSpec
        every { promptSpec.call() } returns callResponseSpec
        every { callResponseSpec.content() } returns response

        return chatClient
    }

    @Nested
    inner class `MEMO_MANAGEMENT 의도 분류 테스트` {
        @Test
        fun `응답에 MEMO_MANAGEMENT가 포함되면 MEMO_MANAGEMENT로 분류된다`() {
            // given
            val chatClient = createMockChatClient("MEMO_MANAGEMENT")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("5시에 약속있다")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `응답에 memo_management가 포함되면 MEMO_MANAGEMENT로 분류된다 (대소문자 무시)`() {
            // given
            val chatClient = createMockChatClient("memo_management")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("내일 회의다")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `응답에 Memo_Management가 포함되면 MEMO_MANAGEMENT로 분류된다 (대소문자 무시)`() {
            // given
            val chatClient = createMockChatClient("Memo_Management")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("우유 사야함")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `응답에 MEMO_CREATION이 포함되면 MEMO_MANAGEMENT로 분류된다 (하위 호환)`() {
            // given
            val chatClient = createMockChatClient("MEMO_CREATION")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("메모 저장해줘")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `응답에 memo_creation이 포함되면 MEMO_MANAGEMENT로 분류된다 (하위 호환, 대소문자 무시)`() {
            // given
            val chatClient = createMockChatClient("memo_creation")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("메모 기록해줘")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `응답에 여러 키워드가 있을 때 MEMO_MANAGEMENT가 우선된다`() {
            // given
            val chatClient = createMockChatClient("MEMO_MANAGEMENT and QUESTION")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("테스트 메시지")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }
    }

    @Nested
    inner class `QUESTION 의도 분류 테스트` {
        @Test
        fun `응답에 QUESTION이 포함되면 QUESTION으로 분류된다`() {
            // given
            val chatClient = createMockChatClient("QUESTION")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("내일 뭐있냐")

            // then
            intent shouldBe Intent.QUESTION
        }

        @Test
        fun `응답에 question이 포함되면 QUESTION으로 분류된다 (대소문자 무시)`() {
            // given
            val chatClient = createMockChatClient("question")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("회의 언제야?")

            // then
            intent shouldBe Intent.QUESTION
        }

        @Test
        fun `응답에 Question이 포함되면 QUESTION으로 분류된다 (대소문자 무시)`() {
            // given
            val chatClient = createMockChatClient("Question")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("약속 뭐있어?")

            // then
            intent shouldBe Intent.QUESTION
        }
    }

    @Nested
    inner class `GENERAL_CHAT 의도 분류 테스트` {
        @Test
        fun `매칭되지 않는 응답은 GENERAL_CHAT으로 분류된다`() {
            // given
            val chatClient = createMockChatClient("GENERAL_CHAT")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("안녕")

            // then
            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `알 수 없는 응답은 GENERAL_CHAT으로 분류된다`() {
            // given
            val chatClient = createMockChatClient("UNKNOWN")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("고마워")

            // then
            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `빈 응답은 GENERAL_CHAT으로 분류된다`() {
            // given
            val chatClient = createMockChatClient("")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("뭐해?")

            // then
            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `null 응답은 GENERAL_CHAT으로 분류된다`() {
            // given
            val chatClient = createMockChatClient(null)
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("테스트")

            // then
            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `공백만 있는 응답은 GENERAL_CHAT으로 분류된다`() {
            // given
            val chatClient = createMockChatClient("   ")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("공백 테스트")

            // then
            intent shouldBe Intent.GENERAL_CHAT
        }
    }

    @Nested
    inner class `우선순위 테스트` {
        @Test
        fun `MEMO_MANAGEMENT는 QUESTION보다 우선순위가 높다`() {
            // given
            val chatClient = createMockChatClient("MEMO_MANAGEMENT QUESTION")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("테스트")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `MEMO_CREATION은 QUESTION보다 우선순위가 높다`() {
            // given
            val chatClient = createMockChatClient("QUESTION MEMO_CREATION")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("테스트")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }
    }

    @Nested
    inner class `엣지 케이스 테스트` {
        @Test
        fun `응답에 키워드가 부분 문자열로 포함되어도 매칭된다`() {
            // given
            val chatClient = createMockChatClient("The intent is MEMO_MANAGEMENT for this case")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("테스트")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `응답에 여러 줄이 있어도 정상 분류된다`() {
            // given
            val chatClient = createMockChatClient("Intent:\nQUESTION")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("테스트")

            // then
            intent shouldBe Intent.QUESTION
        }

        @Test
        fun `응답에 특수문자가 포함되어도 정상 분류된다`() {
            // given
            val chatClient = createMockChatClient("[MEMO_MANAGEMENT]")
            val intentRouter = IntentRouterAgent(chatClient)

            // when
            val intent = intentRouter.classify("테스트")

            // then
            intent shouldBe Intent.MEMO_MANAGEMENT
        }
    }
}
