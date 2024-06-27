package kr.co.jiniaslog

import io.mockk.mockk
import kr.co.jiniaslog.media.outbound.ImageUploader
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestContextConfig {
    @Bean
    @Primary
    fun imageUploader(): ImageUploader {
        return mockk()
    }
}
