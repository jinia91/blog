package kr.co.jiniaslog.ai.domain.agent

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kr.co.jiniaslog.shared.SimpleUnitTestContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AgentResponseTests : SimpleUnitTestContext() {

    @Nested
    inner class `ChatResponse 테스트` {
        @Test
        fun `content로 생성할 수 있다`() {
            val response = AgentResponse.ChatResponse("안녕하세요")

            response.content shouldBe "안녕하세요"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.ChatResponse("테스트")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }

    @Nested
    inner class `MemoCreated 테스트` {
        @Test
        fun `모든 필드로 생성할 수 있다`() {
            val response = AgentResponse.MemoCreated(
                memoId = 1L,
                title = "새 메모",
                message = "메모가 생성되었습니다"
            )

            response.memoId shouldBe 1L
            response.title shouldBe "새 메모"
            response.message shouldBe "메모가 생성되었습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.MemoCreated(1L, "제목", "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }

    @Nested
    inner class `MemoUpdated 테스트` {
        @Test
        fun `memoId와 message로 생성할 수 있다`() {
            val response = AgentResponse.MemoUpdated(
                memoId = 1L,
                message = "메모가 수정되었습니다"
            )

            response.memoId shouldBe 1L
            response.message shouldBe "메모가 수정되었습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.MemoUpdated(1L, "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }

    @Nested
    inner class `MemoMoved 테스트` {
        @Test
        fun `folderId를 포함하여 생성할 수 있다`() {
            val response = AgentResponse.MemoMoved(
                memoId = 1L,
                folderId = 2L,
                message = "메모가 이동되었습니다"
            )

            response.memoId shouldBe 1L
            response.folderId shouldBe 2L
            response.message shouldBe "메모가 이동되었습니다"
        }

        @Test
        fun `folderId가 null인 경우 생성할 수 있다`() {
            val response = AgentResponse.MemoMoved(
                memoId = 1L,
                folderId = null,
                message = "메모가 루트로 이동되었습니다"
            )

            response.memoId shouldBe 1L
            response.folderId.shouldBeNull()
            response.message shouldBe "메모가 루트로 이동되었습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.MemoMoved(1L, 2L, "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }

    @Nested
    inner class `FolderCreated 테스트` {
        @Test
        fun `모든 필드로 생성할 수 있다`() {
            val response = AgentResponse.FolderCreated(
                folderId = 1L,
                name = "새 폴더",
                message = "폴더가 생성되었습니다"
            )

            response.folderId shouldBe 1L
            response.name shouldBe "새 폴더"
            response.message shouldBe "폴더가 생성되었습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.FolderCreated(1L, "폴더명", "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }

    @Nested
    inner class `FolderRenamed 테스트` {
        @Test
        fun `모든 필드로 생성할 수 있다`() {
            val response = AgentResponse.FolderRenamed(
                folderId = 1L,
                name = "변경된 폴더명",
                message = "폴더명이 변경되었습니다"
            )

            response.folderId shouldBe 1L
            response.name shouldBe "변경된 폴더명"
            response.message shouldBe "폴더명이 변경되었습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.FolderRenamed(1L, "폴더명", "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }

    @Nested
    inner class `FolderMoved 테스트` {
        @Test
        fun `parentFolderId를 포함하여 생성할 수 있다`() {
            val response = AgentResponse.FolderMoved(
                folderId = 1L,
                parentFolderId = 2L,
                message = "폴더가 이동되었습니다"
            )

            response.folderId shouldBe 1L
            response.parentFolderId shouldBe 2L
            response.message shouldBe "폴더가 이동되었습니다"
        }

        @Test
        fun `parentFolderId가 null인 경우 생성할 수 있다`() {
            val response = AgentResponse.FolderMoved(
                folderId = 1L,
                parentFolderId = null,
                message = "폴더가 루트로 이동되었습니다"
            )

            response.folderId shouldBe 1L
            response.parentFolderId.shouldBeNull()
            response.message shouldBe "폴더가 루트로 이동되었습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.FolderMoved(1L, 2L, "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }

    @Nested
    inner class `Deleted 테스트` {
        @Test
        fun `MEMO 타입으로 생성할 수 있다`() {
            val response = AgentResponse.Deleted(
                type = AgentResponse.Deleted.DeleteType.MEMO,
                targetId = 1L,
                message = "메모가 삭제되었습니다"
            )

            response.type shouldBe AgentResponse.Deleted.DeleteType.MEMO
            response.targetId shouldBe 1L
            response.message shouldBe "메모가 삭제되었습니다"
        }

        @Test
        fun `FOLDER 타입으로 생성할 수 있다`() {
            val response = AgentResponse.Deleted(
                type = AgentResponse.Deleted.DeleteType.FOLDER,
                targetId = 2L,
                message = "폴더가 삭제되었습니다"
            )

            response.type shouldBe AgentResponse.Deleted.DeleteType.FOLDER
            response.targetId shouldBe 2L
            response.message shouldBe "폴더가 삭제되었습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.Deleted(
                AgentResponse.Deleted.DeleteType.MEMO,
                1L,
                "메시지"
            )

            response.shouldBeInstanceOf<AgentResponse>()
        }

        @Nested
        inner class `DeleteType enum 테스트` {
            @Test
            fun `MEMO 값이 존재한다`() {
                val type = AgentResponse.Deleted.DeleteType.MEMO

                type shouldBe AgentResponse.Deleted.DeleteType.MEMO
            }

            @Test
            fun `FOLDER 값이 존재한다`() {
                val type = AgentResponse.Deleted.DeleteType.FOLDER

                type shouldBe AgentResponse.Deleted.DeleteType.FOLDER
            }
        }
    }

    @Nested
    inner class `MemoList 테스트` {
        @Test
        fun `빈 리스트로 생성할 수 있다`() {
            val response = AgentResponse.MemoList(
                memos = emptyList(),
                message = "메모가 없습니다"
            )

            response.memos.shouldBeEmpty()
            response.message shouldBe "메모가 없습니다"
        }

        @Test
        fun `메모 목록으로 생성할 수 있다`() {
            val memos = listOf(
                AgentResponse.MemoList.MemoSummary(1L, "메모1", null),
                AgentResponse.MemoList.MemoSummary(2L, "메모2", 1L)
            )
            val response = AgentResponse.MemoList(
                memos = memos,
                message = "2개의 메모를 찾았습니다"
            )

            response.memos shouldHaveSize 2
            response.memos[0].id shouldBe 1L
            response.memos[0].title shouldBe "메모1"
            response.memos[0].folderId.shouldBeNull()
            response.memos[1].id shouldBe 2L
            response.memos[1].title shouldBe "메모2"
            response.memos[1].folderId shouldBe 1L
            response.message shouldBe "2개의 메모를 찾았습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.MemoList(emptyList(), "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }

        @Nested
        inner class `MemoSummary 테스트` {
            @Test
            fun `모든 필드로 생성할 수 있다`() {
                val summary = AgentResponse.MemoList.MemoSummary(
                    id = 1L,
                    title = "메모 제목",
                    folderId = 2L
                )

                summary.id shouldBe 1L
                summary.title shouldBe "메모 제목"
                summary.folderId shouldBe 2L
            }

            @Test
            fun `folderId가 null인 경우 생성할 수 있다`() {
                val summary = AgentResponse.MemoList.MemoSummary(
                    id = 1L,
                    title = "메모 제목",
                    folderId = null
                )

                summary.id shouldBe 1L
                summary.title shouldBe "메모 제목"
                summary.folderId.shouldBeNull()
            }
        }
    }

    @Nested
    inner class `FolderList 테스트` {
        @Test
        fun `빈 리스트로 생성할 수 있다`() {
            val response = AgentResponse.FolderList(
                folders = emptyList(),
                message = "폴더가 없습니다"
            )

            response.folders.shouldBeEmpty()
            response.message shouldBe "폴더가 없습니다"
        }

        @Test
        fun `폴더 목록으로 생성할 수 있다`() {
            val folders = listOf(
                AgentResponse.FolderList.FolderSummary(1L, "폴더1", null),
                AgentResponse.FolderList.FolderSummary(2L, "폴더2", 1L)
            )
            val response = AgentResponse.FolderList(
                folders = folders,
                message = "2개의 폴더를 찾았습니다"
            )

            response.folders shouldHaveSize 2
            response.folders[0].id shouldBe 1L
            response.folders[0].name shouldBe "폴더1"
            response.folders[0].parentFolderId.shouldBeNull()
            response.folders[1].id shouldBe 2L
            response.folders[1].name shouldBe "폴더2"
            response.folders[1].parentFolderId shouldBe 1L
            response.message shouldBe "2개의 폴더를 찾았습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.FolderList(emptyList(), "메시지")

            response.shouldBeInstanceOf<AgentResponse>()
        }

        @Nested
        inner class `FolderSummary 테스트` {
            @Test
            fun `모든 필드로 생성할 수 있다`() {
                val summary = AgentResponse.FolderList.FolderSummary(
                    id = 1L,
                    name = "폴더명",
                    parentFolderId = 2L
                )

                summary.id shouldBe 1L
                summary.name shouldBe "폴더명"
                summary.parentFolderId shouldBe 2L
            }

            @Test
            fun `parentFolderId가 null인 경우 생성할 수 있다`() {
                val summary = AgentResponse.FolderList.FolderSummary(
                    id = 1L,
                    name = "폴더명",
                    parentFolderId = null
                )

                summary.id shouldBe 1L
                summary.name shouldBe "폴더명"
                summary.parentFolderId.shouldBeNull()
            }
        }
    }

    @Nested
    inner class `Error 테스트` {
        @Test
        fun `message로 생성할 수 있다`() {
            val response = AgentResponse.Error("오류가 발생했습니다")

            response.message shouldBe "오류가 발생했습니다"
        }

        @Test
        fun `AgentResponse 타입이다`() {
            val response = AgentResponse.Error("오류")

            response.shouldBeInstanceOf<AgentResponse>()
        }
    }
}

class IntentTests : SimpleUnitTestContext() {

    @Nested
    inner class `Intent enum 테스트` {
        @Test
        fun `QUESTION 값이 존재한다`() {
            val intent = Intent.QUESTION

            intent shouldBe Intent.QUESTION
        }

        @Test
        fun `MEMO_MANAGEMENT 값이 존재한다`() {
            val intent = Intent.MEMO_MANAGEMENT

            intent shouldBe Intent.MEMO_MANAGEMENT
        }

        @Test
        fun `GENERAL_CHAT 값이 존재한다`() {
            val intent = Intent.GENERAL_CHAT

            intent shouldBe Intent.GENERAL_CHAT
        }

        @Test
        fun `모든 값이 3개 존재한다`() {
            val values = Intent.entries

            values shouldHaveSize 3
        }
    }
}
