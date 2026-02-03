package kr.co.jiniaslog.ai.adapter.inbound.message

import kr.co.jiniaslog.ai.usecase.IDeleteMemoEmbedding
import kr.co.jiniaslog.ai.usecase.ISyncMemoToEmbedding
import kr.co.jiniaslog.memo.domain.memo.MemoCreatedEvent
import kr.co.jiniaslog.memo.domain.memo.MemoDeletedEvent
import kr.co.jiniaslog.memo.domain.memo.MemoUpdatedEvent
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

private val logger = KotlinLogging.logger {}

@Component
class MemoEventListener(
    private val syncMemoUseCase: ISyncMemoToEmbedding,
    private val deleteMemoEmbeddingUseCase: IDeleteMemoEmbedding,
) {

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

    @Async("aiEmbeddingExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMemoUpdated(event: MemoUpdatedEvent) {
        logger.info { "Handling MemoUpdatedEvent: memoId=${event.memoId}" }
        try {
            syncMemoUseCase(
                ISyncMemoToEmbedding.Command(
                    memoId = event.memoId,
                    authorId = event.authorId,
                    title = event.title,
                    content = event.content,
                )
            )
            logger.info { "Successfully updated memo ${event.memoId} in embedding store" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to update memo ${event.memoId} in embedding store" }
        }
    }

    @Async("aiEmbeddingExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleMemoDeleted(event: MemoDeletedEvent) {
        logger.info { "Handling MemoDeletedEvent: memoId=${event.memoId}" }
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
