package kr.co.jiniaslog.shared.adapter.out.rdb

import com.p6spy.engine.spy.P6SpyOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class SpyConfig {
    @PostConstruct
    fun setLogFormat() {
        P6SpyOptions.getActiveInstance().logMessageFormat = SqlFormatter::class.java.name
        P6SpyOptions.getActiveInstance().append = false
        P6SpyOptions.getActiveInstance().logfile = "dev.null"
    }
}
