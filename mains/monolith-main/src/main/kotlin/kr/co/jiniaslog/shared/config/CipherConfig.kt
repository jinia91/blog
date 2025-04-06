package kr.co.jiniaslog.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class CipherConfig {
    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()
}
