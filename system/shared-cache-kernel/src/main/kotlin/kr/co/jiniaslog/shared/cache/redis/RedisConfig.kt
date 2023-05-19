package kr.co.jiniaslog.shared.cache.redis

import mu.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.JsonJacksonCodec
import org.redisson.config.Config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = KotlinLogging.logger { }

@Configuration
open class RedissonConfig {
    @Bean
    open fun redissonClient(address: RedisAddress): RedissonClient {
        val config = Config()
        config.codec = JsonJacksonCodec()
        config.useSingleServer().apply {
            this.address = address.address
            this.connectionPoolSize = 20
            this.connectionMinimumIdleSize = 20
        }

        return Redisson.create(config)
    }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.redis")
class RedisAddress(
    private val host: String,
    private val port: String,
) {
    val address = "redis://${this.host}:${this.port}"
}
