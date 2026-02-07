package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * MemoManagementAgent의 parseResponse 메서드를 테스트합니다.
 * companion object의 internal 메서드를 직접 테스트합니다.
 */
class MemoManagementAgentTests : SimpleUnitTestContext() {

    private fun parseResponse(response: String): AgentResponse {
        return MemoManagementAgent.parseResponse(response)
    }

    @Nested
    inner class `MEMO_CREATED 파싱 테스트` {
        @Test
        fun `정상적인 형식을 파싱할 수 있다`() {
            // given
            val response = "MEMO_CREATED:123:샘플 메모:메모가 생성되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoCreated>()
            val memoCreated = result as AgentResponse.MemoCreated
            memoCreated.memoId shouldBe 123L
            memoCreated.title shouldBe "샘플 메모"
            memoCreated.message shouldBe "메모가 생성되었습니다."
        }

        @Test
        fun `제목에 콜론이 없고 메시지에만 콜론이 있으면 정상 파싱된다`() {
            // given
            val response = "MEMO_CREATED:456:할일메모:오늘의 할일: 공부하기"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoCreated>()
            val memoCreated = result as AgentResponse.MemoCreated
            memoCreated.memoId shouldBe 456L
            memoCreated.title shouldBe "할일메모"
            memoCreated.message shouldBe "오늘의 할일: 공부하기"
        }

        @Test
        fun `잘못된 형식은 ChatResponse로 반환된다`() {
            // given
            val response = "MEMO_CREATED:123:제목만있음"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            (result as AgentResponse.ChatResponse).content shouldBe response
        }

        @Test
        fun `ID가 숫자가 아니면 0으로 처리된다`() {
            // given
            val response = "MEMO_CREATED:abc:제목:메시지"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoCreated>()
            val memoCreated = result as AgentResponse.MemoCreated
            memoCreated.memoId shouldBe 0L
        }
    }

    @Nested
    inner class `MEMO_UPDATED 파싱 테스트` {
        @Test
        fun `정상적인 형식을 파싱할 수 있다`() {
            // given
            val response = "MEMO_UPDATED:789:메모가 수정되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoUpdated>()
            val memoUpdated = result as AgentResponse.MemoUpdated
            memoUpdated.memoId shouldBe 789L
            memoUpdated.message shouldBe "메모가 수정되었습니다."
        }

        @Test
        fun `메시지가 없으면 기본 메시지가 사용된다`() {
            // given
            val response = "MEMO_UPDATED:100"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoUpdated>()
            val memoUpdated = result as AgentResponse.MemoUpdated
            memoUpdated.memoId shouldBe 100L
            memoUpdated.message shouldBe "메모가 수정되었습니다."
        }

        @Test
        fun `메시지에 콜론이 포함되어도 정상 파싱된다`() {
            // given
            val response = "MEMO_UPDATED:200:내용: 변경됨"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoUpdated>()
            val memoUpdated = result as AgentResponse.MemoUpdated
            memoUpdated.message shouldBe "내용: 변경됨"
        }
    }

    @Nested
    inner class `MEMO_MOVED 파싱 테스트` {
        @Test
        fun `정상적인 형식을 파싱할 수 있다`() {
            // given
            val response = "MEMO_MOVED:111:222:메모가 이동되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoMoved>()
            val memoMoved = result as AgentResponse.MemoMoved
            memoMoved.memoId shouldBe 111L
            memoMoved.folderId shouldBe 222L
            memoMoved.message shouldBe "메모가 이동되었습니다."
        }

        @Test
        fun `folderId가 0이면 null로 처리된다`() {
            // given
            val response = "MEMO_MOVED:333:0:루트로 이동되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoMoved>()
            val memoMoved = result as AgentResponse.MemoMoved
            memoMoved.memoId shouldBe 333L
            memoMoved.folderId.shouldBeNull()
            memoMoved.message shouldBe "루트로 이동되었습니다."
        }

        @Test
        fun `메시지가 없으면 기본 메시지가 사용된다`() {
            // given
            val response = "MEMO_MOVED:444:555"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoMoved>()
            val memoMoved = result as AgentResponse.MemoMoved
            memoMoved.message shouldBe "메모가 이동되었습니다."
        }
    }

    @Nested
    inner class `FOLDER_CREATED 파싱 테스트` {
        @Test
        fun `정상적인 형식을 파싱할 수 있다`() {
            // given
            val response = "FOLDER_CREATED:10:업무:폴더가 생성되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderCreated>()
            val folderCreated = result as AgentResponse.FolderCreated
            folderCreated.folderId shouldBe 10L
            folderCreated.name shouldBe "업무"
            folderCreated.message shouldBe "폴더가 생성되었습니다."
        }

        @Test
        fun `메시지가 없으면 기본 메시지가 사용된다`() {
            // given
            val response = "FOLDER_CREATED:20:개인"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderCreated>()
            val folderCreated = result as AgentResponse.FolderCreated
            folderCreated.folderId shouldBe 20L
            folderCreated.name shouldBe "개인"
            folderCreated.message shouldBe "폴더가 생성되었습니다."
        }
    }

    @Nested
    inner class `FOLDER_RENAMED 파싱 테스트` {
        @Test
        fun `정상적인 형식을 파싱할 수 있다`() {
            // given
            val response = "FOLDER_RENAMED:30:새이름:폴더 이름이 변경되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderRenamed>()
            val folderRenamed = result as AgentResponse.FolderRenamed
            folderRenamed.folderId shouldBe 30L
            folderRenamed.name shouldBe "새이름"
            folderRenamed.message shouldBe "폴더 이름이 변경되었습니다."
        }

        @Test
        fun `메시지가 없으면 기본 메시지가 사용된다`() {
            // given
            val response = "FOLDER_RENAMED:40:변경된폴더"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderRenamed>()
            val folderRenamed = result as AgentResponse.FolderRenamed
            folderRenamed.message shouldBe "폴더 이름이 변경되었습니다."
        }
    }

    @Nested
    inner class `FOLDER_MOVED 파싱 테스트` {
        @Test
        fun `정상적인 형식을 파싱할 수 있다`() {
            // given
            val response = "FOLDER_MOVED:50:60:폴더가 이동되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderMoved>()
            val folderMoved = result as AgentResponse.FolderMoved
            folderMoved.folderId shouldBe 50L
            folderMoved.parentFolderId shouldBe 60L
            folderMoved.message shouldBe "폴더가 이동되었습니다."
        }

        @Test
        fun `parentFolderId가 0이면 null로 처리된다`() {
            // given
            val response = "FOLDER_MOVED:70:0:최상위로 이동되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderMoved>()
            val folderMoved = result as AgentResponse.FolderMoved
            folderMoved.folderId shouldBe 70L
            folderMoved.parentFolderId.shouldBeNull()
            folderMoved.message shouldBe "최상위로 이동되었습니다."
        }

        @Test
        fun `메시지가 없으면 기본 메시지가 사용된다`() {
            // given
            val response = "FOLDER_MOVED:80:90"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderMoved>()
            val folderMoved = result as AgentResponse.FolderMoved
            folderMoved.message shouldBe "폴더가 이동되었습니다."
        }
    }

    @Nested
    inner class `DELETED 파싱 테스트` {
        @Test
        fun `MEMO 삭제를 정상 파싱할 수 있다`() {
            // given
            val response = "DELETED:MEMO:100:메모가 삭제되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.Deleted>()
            val deleted = result as AgentResponse.Deleted
            deleted.type shouldBe AgentResponse.Deleted.DeleteType.MEMO
            deleted.targetId shouldBe 100L
            deleted.message shouldBe "메모가 삭제되었습니다."
        }

        @Test
        fun `FOLDER 삭제를 정상 파싱할 수 있다`() {
            // given
            val response = "DELETED:FOLDER:200:폴더가 삭제되었습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.Deleted>()
            val deleted = result as AgentResponse.Deleted
            deleted.type shouldBe AgentResponse.Deleted.DeleteType.FOLDER
            deleted.targetId shouldBe 200L
            deleted.message shouldBe "폴더가 삭제되었습니다."
        }

        @Test
        fun `잘못된 형식은 ChatResponse로 반환된다`() {
            // given
            val response = "DELETED:MEMO:300" // 메시지 없음

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            (result as AgentResponse.ChatResponse).content shouldBe response
        }

        @Test
        fun `TYPE이 MEMO가 아니면 FOLDER로 처리된다`() {
            // given
            val response = "DELETED:UNKNOWN:400:알 수 없는 타입 삭제"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.Deleted>()
            val deleted = result as AgentResponse.Deleted
            deleted.type shouldBe AgentResponse.Deleted.DeleteType.FOLDER
        }
    }

    @Nested
    inner class `MEMO_LIST 파싱 테스트` {
        @Test
        fun `메모가 없는 경우를 파싱할 수 있다`() {
            // given
            val response = "MEMO_LIST:0:메모가 없습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoList>()
            val memoList = result as AgentResponse.MemoList
            memoList.memos.shouldBeEmpty()
            memoList.message shouldBe "메모가 없습니다."
        }

        @Test
        fun `메모 목록을 파싱할 수 있다`() {
            // given
            val response = """MEMO_LIST:3:[1] 첫번째 메모
[2] 두번째 메모 (폴더: 10)
[3] 세번째 메모"""

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoList>()
            val memoList = result as AgentResponse.MemoList
            memoList.memos shouldHaveSize 3
            memoList.memos[0].id shouldBe 1L
            memoList.memos[0].title shouldBe "첫번째 메모"
            memoList.memos[0].folderId.shouldBeNull()
            memoList.memos[1].id shouldBe 2L
            memoList.memos[1].title shouldBe "두번째 메모"
            memoList.memos[1].folderId shouldBe 10L
            memoList.memos[2].id shouldBe 3L
            memoList.memos[2].title shouldBe "세번째 메모"
            memoList.memos[2].folderId.shouldBeNull()
            memoList.message shouldBe "3 개의 메모가 있습니다.\n[1] 첫번째 메모\n[2] 두번째 메모 (폴더: 10)\n[3] 세번째 메모"
        }

        @Test
        fun `메모 제목에 공백이 있어도 trim 처리된다`() {
            // given
            val response = """MEMO_LIST:1:[100]   공백있는 제목   """

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.MemoList>()
            val memoList = result as AgentResponse.MemoList
            memoList.memos shouldHaveSize 1
            memoList.memos[0].title shouldBe "공백있는 제목"
        }
    }

    @Nested
    inner class `FOLDER_LIST 파싱 테스트` {
        @Test
        fun `폴더가 없는 경우를 파싱할 수 있다`() {
            // given
            val response = "FOLDER_LIST:0:폴더가 없습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderList>()
            val folderList = result as AgentResponse.FolderList
            folderList.folders.shouldBeEmpty()
            folderList.message shouldBe "폴더가 없습니다."
        }

        @Test
        fun `폴더 목록을 파싱할 수 있다`() {
            // given
            val response = """FOLDER_LIST:3:[1] 업무 (최상위)
[2] 개인 (상위: 1)
[3] 프로젝트"""

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderList>()
            val folderList = result as AgentResponse.FolderList
            folderList.folders shouldHaveSize 3
            folderList.folders[0].id shouldBe 1L
            folderList.folders[0].name shouldBe "업무"
            folderList.folders[0].parentFolderId.shouldBeNull()
            folderList.folders[1].id shouldBe 2L
            folderList.folders[1].name shouldBe "개인"
            folderList.folders[1].parentFolderId shouldBe 1L
            folderList.folders[2].id shouldBe 3L
            folderList.folders[2].name shouldBe "프로젝트"
            folderList.folders[2].parentFolderId.shouldBeNull()
            folderList.message shouldBe "3 개의 폴더가 있습니다.\n[1] 업무 (최상위)\n[2] 개인 (상위: 1)\n[3] 프로젝트"
        }

        @Test
        fun `폴더 이름에 공백이 있어도 trim 처리된다`() {
            // given
            val response = """FOLDER_LIST:1:[50]   공백있는폴더   (최상위)"""

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.FolderList>()
            val folderList = result as AgentResponse.FolderList
            folderList.folders shouldHaveSize 1
            folderList.folders[0].name shouldBe "공백있는폴더"
        }
    }

    @Nested
    inner class `ERROR 파싱 테스트` {
        @Test
        fun `에러 메시지를 파싱할 수 있다`() {
            // given
            val response = "ERROR:메모를 찾을 수 없습니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.Error>()
            val error = result as AgentResponse.Error
            error.message shouldBe "메모를 찾을 수 없습니다."
        }

        @Test
        fun `에러 메시지의 공백은 trim 처리된다`() {
            // given
            val response = "ERROR:   공백이 있는 에러   "

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.Error>()
            val error = result as AgentResponse.Error
            error.message shouldBe "공백이 있는 에러"
        }
    }

    @Nested
    inner class `일반 응답 파싱 테스트` {
        @Test
        fun `매칭되지 않는 응답은 ChatResponse로 반환된다`() {
            // given
            val response = "일반적인 채팅 응답입니다."

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            val chatResponse = result as AgentResponse.ChatResponse
            chatResponse.content shouldBe "일반적인 채팅 응답입니다."
        }

        @Test
        fun `삭제 확인 질문도 ChatResponse로 반환된다`() {
            // given
            val response = "정말로 삭제하시겠습니까?"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            val chatResponse = result as AgentResponse.ChatResponse
            chatResponse.content shouldBe "정말로 삭제하시겠습니까?"
        }

        @Test
        fun `알 수 없는 프리픽스는 ChatResponse로 반환된다`() {
            // given
            val response = "UNKNOWN_PREFIX:데이터"

            // when
            val result = parseResponse(response)

            // then
            result.shouldBeInstanceOf<AgentResponse.ChatResponse>()
            val chatResponse = result as AgentResponse.ChatResponse
            chatResponse.content shouldBe "UNKNOWN_PREFIX:데이터"
        }
    }
}
