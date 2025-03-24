package kr.co.jiniaslog.memo.adapter.inbound.batch

import kr.co.jiniaslog.memo.usecase.FolderUseCasesFacade
import kr.co.jiniaslog.memo.usecase.IDeleteAllWithoutAdmin
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val log = mu.KotlinLogging.logger {}

@Component
class MemoBatchTask(
    private val folderUseCasesFacade: FolderUseCasesFacade
) {
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul") // 매주 월요일 0시에 실행
    fun execute() {
        log.info { "IDeleteAllWithoutAdmin 주기적 메모 삭제 실행" }
        folderUseCasesFacade.handle(IDeleteAllWithoutAdmin.Command())
        log.info { "IDeleteAllWithoutAdmin 주기적 메모 삭제 완료" }
    }
}
