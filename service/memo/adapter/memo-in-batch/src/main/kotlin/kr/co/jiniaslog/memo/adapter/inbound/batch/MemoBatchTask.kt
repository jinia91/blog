package kr.co.jiniaslog.memo.adapter.inbound.batch

import kr.co.jiniaslog.memo.usecase.FolderUseCasesFacade
import kr.co.jiniaslog.memo.usecase.IDeleteAllWithoutAdmin
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MemoBatchTask(
    private val folderUseCasesFacade: FolderUseCasesFacade
) {
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 0시 0분 0초
    fun execute() {
        folderUseCasesFacade.handle(IDeleteAllWithoutAdmin.Command())
    }
}
