package kr.co.jiniaslog.memo.adapter.inbound.batch

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MemoBatchTask() {
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 0시 0분 0초
    fun execute() {
    }
}
