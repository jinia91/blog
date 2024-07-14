package kr.co.jiniaslog.blog.adapter.inbound.batch

import kr.co.jiniaslog.blog.usecase.tag.IDeleteUnUsedTags
import org.springframework.scheduling.annotation.Scheduled

class TagBatchTask(
    private val deleteUnUsedTagsUseCase: IDeleteUnUsedTags
) {
    // 매일 0시에 실행
    @Scheduled(cron = "0 0 0 * * *")
    fun execute() {
        deleteUnUsedTagsUseCase.handle(IDeleteUnUsedTags.Command())
    }
}
