package kr.co.jiniaslog.ai.adapter.inbound.message

import kr.co.jiniaslog.ai.usecase.IDeleteMemoEmbedding
import kr.co.jiniaslog.ai.usecase.ISyncMemoToEmbedding
import kr.co.jiniaslog.memo.domain.memo.MemoCreatedEvent
import kr.co.jiniaslog.memo.domain.memo.MemoDeletedEvent
import kr.co.jiniaslog.memo.domain.memo.MemoUpdatedEvent
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Component
class MemoEventListener(
    private val syncMemoUseCase: ISyncMemoToEmbedding,
    private val deleteMemoEmbeddingUseCase: IDeleteMemoEmbedding,
    @Qualifier("embeddingScheduler")
    private val scheduler: ScheduledExecutorService,
) {
    companion object {
        private const val DEBOUNCE_DELAY_SECONDS = 5L
    }

    // 메모별 디바운스 타이머
    private val pendingUpdates = ConcurrentHashMap<Long, ScheduledFuture<*>>()
    private val pendingEvents = ConcurrentHashMap<Long, MemoUpdatedEvent>()

    @Async("aiEmbeddingExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMemoCreated(event: MemoCreatedEvent) {
        logger.info { "Handling MemoCreatedEvent: memoId=${event.memoId}" }
        try {
            syncMemoUseCase(
                ISyncMemoToEmbedding.Command(
                    memoId = event.memoId,
                    authorId = event.authorId,
                    title = event.title,
                    content = event.content,
                )
            )
            logger.info { "Successfully synced memo ${event.memoId} to embedding store" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to sync memo ${event.memoId} to embedding store" }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMemoUpdated(event: MemoUpdatedEvent) {
        logger.info { "Scheduling debounced update for memoId=${event.memoId}" }

        // 이전 스케줄 취소
        pendingUpdates[event.memoId]?.cancel(false)
        pendingEvents[event.memoId] = event

        // 디바운스: 5초 후 동기화
        val future = scheduler.schedule({
            val latestEvent = pendingEvents.remove(event.memoId)
            pendingUpdates.remove(event.memoId)

            latestEvent?.let { evt ->
                try {
                    syncMemoUseCase(
                        ISyncMemoToEmbedding.Command(
                            memoId = evt.memoId,
                            authorId = evt.authorId,
                            title = evt.title,
                            content = evt.content,
                        )
                    )
                    logger.info { "Successfully updated memo ${evt.memoId} in embedding store" }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to update memo ${evt.memoId} in embedding store" }
                }
            }
        }, DEBOUNCE_DELAY_SECONDS, TimeUnit.SECONDS)

        pendingUpdates[event.memoId] = future
    }

    @Async("aiEmbeddingExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMemoDeleted(event: MemoDeletedEvent) {
        logger.info { "Handling MemoDeletedEvent: memoId=${event.memoId}" }

        // 대기 중인 업데이트 취소
        pendingUpdates.remove(event.memoId)?.cancel(false)
        pendingEvents.remove(event.memoId)

        try {
            deleteMemoEmbeddingUseCase(
                IDeleteMemoEmbedding.Command(memoId = event.memoId)
            )
            logger.info { "Successfully deleted memo ${event.memoId} from embedding store" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to delete memo ${event.memoId} from embedding store" }
        }
    }
}
