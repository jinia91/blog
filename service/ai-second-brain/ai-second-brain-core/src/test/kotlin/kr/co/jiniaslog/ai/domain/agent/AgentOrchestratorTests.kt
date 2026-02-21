package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage

class AgentOrchestratorTests : SimpleUnitTestContext() {

    private val intentRouter = mockk<IntentRouterAgent>()
    private val ragAgent = mockk<RagAgent>()
    private val generalChatAgent = mockk<GeneralChatAgent>()
    private val memoManagementAgent = mockk<MemoManagementAgent>()
    private val compoundIntentHandler = mockk<CompoundIntentHandler>()
    private val chatMemory = mockk<ChatMemory>()

    private val orchestrator = AgentOrchestrator(
        intentRouter = intentRouter,
        ragAgent = ragAgent,
        generalChatAgent = generalChatAgent,
        memoManagementAgent = memoManagementAgent,
        compoundIntentHandler = compoundIntentHandler,
        chatMemory = chatMemory
    )

    @Nested
    inner class `KNOWLEDGE_QUERY 라우팅 테스트` {
        @Test
        fun `KNOWLEDGE_QUERY 의도이면 ragAgent에 위임하고 ChatResponse를 반환한다`() {
            // given
            every { chatMemory.get(any()) } returns emptyList()
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.KNOWLEDGE_QUERY, emptyList())
            every { ragAgent.chat(any(), any(), any()) } returns "RAG 응답입니다."

            // when
            val result = orchestrator.process("내일 뭐있냐", 1L, 100L)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            (result as AgentResponse.ChatResponse).content shouldBe "RAG 응답입니다."
            verify(exactly = 1) { ragAgent.chat("내일 뭐있냐", 1L, 100L) }
        }
    }

    @Nested
    inner class `MEMO_WRITE 라우팅 테스트` {
        @Test
        fun `MEMO_WRITE 의도이면 memoManagementAgent에 위임한다`() {
            // given
            every { chatMemory.get(any()) } returns emptyList()
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.MEMO_WRITE, emptyList())
            every { memoManagementAgent.process(any(), any(), any()) } returns AgentResponse.MemoCreated(1L, "제목", "생성됨")

            // when
            val result = orchestrator.process("5시에 약속있다", 1L, 100L)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoCreated>()
            verify(exactly = 1) { memoManagementAgent.process("5시에 약속있다", 100L, 1L) }
        }
    }

    @Nested
    inner class `MEMO_ORGANIZE 라우팅 테스트` {
        @Test
        fun `MEMO_ORGANIZE 의도이면 memoManagementAgent에 위임한다`() {
            // given
            every { chatMemory.get(any()) } returns emptyList()
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.MEMO_ORGANIZE, emptyList())
            every { memoManagementAgent.process(any(), any(), any()) } returns AgentResponse.FolderCreated(10L, "업무", "생성됨")

            // when
            val result = orchestrator.process("새 폴더 만들어줘", 1L, 100L)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderCreated>()
            verify(exactly = 1) { memoManagementAgent.process("새 폴더 만들어줘", 100L, 1L) }
        }
    }

    @Nested
    inner class `MEMO_SEARCH 라우팅 테스트` {
        @Test
        fun `MEMO_SEARCH 의도이면 memoManagementAgent에 위임한다`() {
            // given
            every { chatMemory.get(any()) } returns emptyList()
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.MEMO_SEARCH, emptyList())
            every { memoManagementAgent.process(any(), any(), any()) } returns AgentResponse.MemoList(emptyList(), "메모 없음")

            // when
            val result = orchestrator.process("메모 목록 보여줘", 1L, 100L)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoList>()
            verify(exactly = 1) { memoManagementAgent.process("메모 목록 보여줘", 100L, 1L) }
        }
    }

    @Nested
    inner class `COMPOUND 라우팅 테스트` {
        @Test
        fun `COMPOUND 의도이면 compoundIntentHandler에 위임한다`() {
            // given
            val subIntents = listOf(Intent.MEMO_WRITE, Intent.KNOWLEDGE_QUERY)
            every { chatMemory.get(any()) } returns emptyList()
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.COMPOUND, subIntents)
            every { compoundIntentHandler.process(any(), any(), any(), any()) } returns AgentResponse.ChatResponse("복합 응답")

            // when
            val result = orchestrator.process("메모하고 관련 자료 찾아줘", 1L, 100L)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            (result as AgentResponse.ChatResponse).content shouldBe "복합 응답"
            verify(exactly = 1) { compoundIntentHandler.process("메모하고 관련 자료 찾아줘", subIntents, 1L, 100L) }
        }
    }

    @Nested
    inner class `GENERAL_CHAT 라우팅 테스트` {
        @Test
        fun `GENERAL_CHAT 의도이면 generalChatAgent에 위임하고 ChatResponse를 반환한다`() {
            // given
            every { chatMemory.get(any()) } returns emptyList()
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.GENERAL_CHAT, emptyList())
            every { generalChatAgent.chat(any(), any()) } returns "안녕하세요!"

            // when
            val result = orchestrator.process("안녕", 1L, 100L)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            (result as AgentResponse.ChatResponse).content shouldBe "안녕하세요!"
            verify(exactly = 1) { generalChatAgent.chat("안녕", 1L) }
        }
    }

    @Nested
    inner class `classifyIntent 테스트` {
        @Test
        fun `classifyIntent는 intentRouter의 classify 결과를 반환한다`() {
            // given
            every { intentRouter.classify(any()) } returns Intent.KNOWLEDGE_QUERY

            // when
            val result = orchestrator.classifyIntent("뭐있어?")

            // then
            result shouldBe Intent.KNOWLEDGE_QUERY
            verify(exactly = 1) { intentRouter.classify("뭐있어?") }
        }
    }

    @Nested
    inner class `buildConversationHistory 테스트` {
        @Test
        fun `chatMemory에 메시지가 있으면 대화 히스토리를 구성하여 intentRouter에 전달한다`() {
            // given
            val userMsg = UserMessage("안녕")
            val assistantMsg = AssistantMessage("안녕하세요!")
            every { chatMemory.get("1") } returns listOf(userMsg, assistantMsg)
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.GENERAL_CHAT, emptyList())
            every { generalChatAgent.chat(any(), any()) } returns "응답"

            // when
            orchestrator.process("뭐해?", 1L, 100L)

            // then - conversationHistory 가 비어있지 않은 상태로 intentRouter 호출
            verify {
                intentRouter.classifyWithSubIntents(
                    "뭐해?",
                    match { it.contains("사용자: 안녕") && it.contains("어시스턴트: 안녕하세요!") }
                )
            }
        }

        @Test
        fun `chatMemory에서 예외가 발생하면 빈 히스토리로 처리한다`() {
            // given
            every { chatMemory.get(any()) } throws RuntimeException("메모리 오류")
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.GENERAL_CHAT, emptyList())
            every { generalChatAgent.chat(any(), any()) } returns "응답"

            // when
            val result = orchestrator.process("안녕", 1L, 100L)

            // then - 예외 발생해도 정상 처리됨
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            verify { intentRouter.classifyWithSubIntents("안녕", "") }
        }

        @Test
        fun `chatMemory가 빈 리스트를 반환하면 빈 히스토리로 처리한다`() {
            // given
            every { chatMemory.get(any()) } returns emptyList()
            every { intentRouter.classifyWithSubIntents(any(), any()) } returns Pair(Intent.GENERAL_CHAT, emptyList())
            every { generalChatAgent.chat(any(), any()) } returns "응답"

            // when
            orchestrator.process("안녕", 1L, 100L)

            // then
            verify { intentRouter.classifyWithSubIntents("안녕", "") }
        }
    }
}
