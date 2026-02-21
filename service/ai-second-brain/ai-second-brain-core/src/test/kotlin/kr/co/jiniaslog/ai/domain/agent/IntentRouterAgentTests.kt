package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.collections.shouldHaveSize
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
    inner class `MEMO_WRITE 의도 분류 테스트` {
        @Test
        fun `응답에 MEMO_WRITE가 포함되면 MEMO_WRITE로 분류된다`() {
            val chatClient = createMockChatClient("MEMO_WRITE")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("5시에 약속있다")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `응답에 memo_write가 포함되면 MEMO_WRITE로 분류된다 (대소문자 무시)`() {
            val chatClient = createMockChatClient("memo_write")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("내일 회의다")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `응답에 MEMO_MANAGEMENT가 포함되면 MEMO_WRITE로 분류된다 (하위 호환)`() {
            val chatClient = createMockChatClient("MEMO_MANAGEMENT")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("메모 저장해줘")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `응답에 MEMO_CREATION이 포함되면 MEMO_WRITE로 분류된다 (하위 호환)`() {
            val chatClient = createMockChatClient("MEMO_CREATION")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("메모 기록해줘")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `응답에 memo_creation이 포함되면 MEMO_WRITE로 분류된다 (하위 호환, 대소문자 무시)`() {
            val chatClient = createMockChatClient("memo_creation")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("노트 적어줘")

            intent shouldBe Intent.MEMO_WRITE
        }
    }

    @Nested
    inner class `MEMO_ORGANIZE 의도 분류 테스트` {
        @Test
        fun `응답에 MEMO_ORGANIZE가 포함되면 MEMO_ORGANIZE로 분류된다`() {
            val chatClient = createMockChatClient("MEMO_ORGANIZE")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("새 폴더 만들어줘")

            intent shouldBe Intent.MEMO_ORGANIZE
        }

        @Test
        fun `응답에 memo_organize가 포함되면 MEMO_ORGANIZE로 분류된다 (대소문자 무시)`() {
            val chatClient = createMockChatClient("memo_organize")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("메모 폴더로 정리해")

            intent shouldBe Intent.MEMO_ORGANIZE
        }
    }

    @Nested
    inner class `MEMO_SEARCH 의도 분류 테스트` {
        @Test
        fun `응답에 MEMO_SEARCH가 포함되면 MEMO_SEARCH로 분류된다`() {
            val chatClient = createMockChatClient("MEMO_SEARCH")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("메모 목록 보여줘")

            intent shouldBe Intent.MEMO_SEARCH
        }

        @Test
        fun `응답에 memo_search가 포함되면 MEMO_SEARCH로 분류된다 (대소문자 무시)`() {
            val chatClient = createMockChatClient("memo_search")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("어떤 메모들 있어?")

            intent shouldBe Intent.MEMO_SEARCH
        }
    }

    @Nested
    inner class `KNOWLEDGE_QUERY 의도 분류 테스트` {
        @Test
        fun `응답에 KNOWLEDGE_QUERY가 포함되면 KNOWLEDGE_QUERY로 분류된다`() {
            val chatClient = createMockChatClient("KNOWLEDGE_QUERY")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("내일 뭐있냐")

            intent shouldBe Intent.KNOWLEDGE_QUERY
        }

        @Test
        fun `응답에 knowledge_query가 포함되면 KNOWLEDGE_QUERY로 분류된다 (대소문자 무시)`() {
            val chatClient = createMockChatClient("knowledge_query")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("회의 언제야?")

            intent shouldBe Intent.KNOWLEDGE_QUERY
        }

        @Test
        fun `응답에 QUESTION이 포함되면 KNOWLEDGE_QUERY로 분류된다 (하위 호환)`() {
            val chatClient = createMockChatClient("QUESTION")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("약속 뭐있어?")

            intent shouldBe Intent.KNOWLEDGE_QUERY
        }

        @Test
        fun `응답에 question이 포함되면 KNOWLEDGE_QUERY로 분류된다 (하위 호환, 대소문자 무시)`() {
            val chatClient = createMockChatClient("question")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("프로젝트 내용 알려줘")

            intent shouldBe Intent.KNOWLEDGE_QUERY
        }
    }

    @Nested
    inner class `GENERAL_CHAT 의도 분류 테스트` {
        @Test
        fun `응답에 GENERAL_CHAT이 포함되면 GENERAL_CHAT으로 분류된다`() {
            val chatClient = createMockChatClient("GENERAL_CHAT")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("안녕")

            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `알 수 없는 응답은 GENERAL_CHAT으로 분류된다`() {
            val chatClient = createMockChatClient("UNKNOWN")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("고마워")

            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `빈 응답은 GENERAL_CHAT으로 분류된다`() {
            val chatClient = createMockChatClient("")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("뭐해?")

            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `null 응답은 GENERAL_CHAT으로 분류된다`() {
            val chatClient = createMockChatClient(null)
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("테스트")

            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `공백만 있는 응답은 GENERAL_CHAT으로 분류된다`() {
            val chatClient = createMockChatClient("   ")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("공백 테스트")

            intent shouldBe Intent.GENERAL_CHAT
        }
    }

    @Nested
    inner class `COMPOUND 의도 분류 테스트` {
        @Test
        fun `COMPOUND 패턴이 있으면 COMPOUND로 분류된다`() {
            val chatClient = createMockChatClient("COMPOUND[MEMO_WRITE,KNOWLEDGE_QUERY]")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("메모하고 관련 자료 찾아줘")

            intent shouldBe Intent.COMPOUND
        }

        @Test
        fun `classifyWithSubIntents는 COMPOUND일 때 서브 의도 목록을 반환한다`() {
            val chatClient = createMockChatClient("COMPOUND[MEMO_WRITE,KNOWLEDGE_QUERY]")
            val intentRouter = IntentRouterAgent(chatClient)

            val (intent, subIntents) = intentRouter.classifyWithSubIntents("메모하고 관련 자료 찾아줘")

            intent shouldBe Intent.COMPOUND
            subIntents shouldHaveSize 2
            subIntents[0] shouldBe Intent.MEMO_WRITE
            subIntents[1] shouldBe Intent.KNOWLEDGE_QUERY
        }

        @Test
        fun `classifyWithSubIntents는 단순 의도일 때 빈 서브 의도 목록을 반환한다`() {
            val chatClient = createMockChatClient("MEMO_WRITE")
            val intentRouter = IntentRouterAgent(chatClient)

            val (intent, subIntents) = intentRouter.classifyWithSubIntents("메모 저장해줘")

            intent shouldBe Intent.MEMO_WRITE
            subIntents shouldHaveSize 0
        }

        @Test
        fun `COMPOUND 대소문자 무시 패턴도 인식된다`() {
            val chatClient = createMockChatClient("compound[MEMO_ORGANIZE,MEMO_WRITE]")
            val intentRouter = IntentRouterAgent(chatClient)

            val (intent, subIntents) = intentRouter.classifyWithSubIntents("폴더 만들고 메모 저장해")

            intent shouldBe Intent.COMPOUND
            subIntents shouldHaveSize 2
        }
    }

    @Nested
    inner class `parseIntentResponse 테스트` {
        private val chatClient = mockk<ChatClient>(relaxed = true)
        private val intentRouter = IntentRouterAgent(chatClient)

        @Test
        fun `MEMO_WRITE 응답을 파싱한다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("MEMO_WRITE")

            intent shouldBe Intent.MEMO_WRITE
            subIntents shouldHaveSize 0
        }

        @Test
        fun `MEMO_ORGANIZE 응답을 파싱한다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("MEMO_ORGANIZE")

            intent shouldBe Intent.MEMO_ORGANIZE
            subIntents shouldHaveSize 0
        }

        @Test
        fun `MEMO_SEARCH 응답을 파싱한다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("MEMO_SEARCH")

            intent shouldBe Intent.MEMO_SEARCH
            subIntents shouldHaveSize 0
        }

        @Test
        fun `KNOWLEDGE_QUERY 응답을 파싱한다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("KNOWLEDGE_QUERY")

            intent shouldBe Intent.KNOWLEDGE_QUERY
            subIntents shouldHaveSize 0
        }

        @Test
        fun `GENERAL_CHAT 응답을 파싱한다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("GENERAL_CHAT")

            intent shouldBe Intent.GENERAL_CHAT
            subIntents shouldHaveSize 0
        }

        @Test
        fun `COMPOUND 응답을 파싱하면 서브 의도 목록이 반환된다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("COMPOUND[MEMO_WRITE,KNOWLEDGE_QUERY]")

            intent shouldBe Intent.COMPOUND
            subIntents shouldHaveSize 2
            subIntents[0] shouldBe Intent.MEMO_WRITE
            subIntents[1] shouldBe Intent.KNOWLEDGE_QUERY
        }

        @Test
        fun `COMPOUND에 서브 의도가 1개면 해당 의도로 분류된다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("COMPOUND[MEMO_WRITE]")

            intent shouldBe Intent.MEMO_WRITE
            subIntents shouldHaveSize 0
        }

        @Test
        fun `알 수 없는 응답은 GENERAL_CHAT으로 파싱된다`() {
            val (intent, subIntents) = intentRouter.parseIntentResponse("UNKNOWN_INTENT")

            intent shouldBe Intent.GENERAL_CHAT
            subIntents shouldHaveSize 0
        }

        @Test
        fun `하위 호환 - MEMO_MANAGEMENT 응답은 MEMO_WRITE로 파싱된다`() {
            val (intent, _) = intentRouter.parseIntentResponse("MEMO_MANAGEMENT")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `하위 호환 - QUESTION 응답은 KNOWLEDGE_QUERY로 파싱된다`() {
            val (intent, _) = intentRouter.parseIntentResponse("QUESTION")

            intent shouldBe Intent.KNOWLEDGE_QUERY
        }
    }

    @Nested
    inner class `우선순위 테스트` {
        @Test
        fun `MEMO_WRITE는 KNOWLEDGE_QUERY보다 우선순위가 높다`() {
            val chatClient = createMockChatClient("MEMO_WRITE KNOWLEDGE_QUERY")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("테스트")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `MEMO_WRITE는 MEMO_ORGANIZE보다 우선순위가 높다`() {
            val chatClient = createMockChatClient("MEMO_WRITE MEMO_ORGANIZE")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("테스트")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `MEMO_ORGANIZE는 MEMO_SEARCH보다 우선순위가 높다`() {
            val chatClient = createMockChatClient("MEMO_ORGANIZE MEMO_SEARCH")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("테스트")

            intent shouldBe Intent.MEMO_ORGANIZE
        }
    }

    @Nested
    inner class `엣지 케이스 테스트` {
        @Test
        fun `응답에 키워드가 부분 문자열로 포함되어도 매칭된다`() {
            val chatClient = createMockChatClient("The intent is MEMO_WRITE for this case")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("테스트")

            intent shouldBe Intent.MEMO_WRITE
        }

        @Test
        fun `응답에 여러 줄이 있어도 정상 분류된다`() {
            val chatClient = createMockChatClient("Intent:\nKNOWLEDGE_QUERY")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("테스트")

            intent shouldBe Intent.KNOWLEDGE_QUERY
        }

        @Test
        fun `응답에 특수문자가 포함되어도 정상 분류된다`() {
            val chatClient = createMockChatClient("[MEMO_WRITE]")
            val intentRouter = IntentRouterAgent(chatClient)

            val intent = intentRouter.classify("테스트")

            intent shouldBe Intent.MEMO_WRITE
        }
    }
}
