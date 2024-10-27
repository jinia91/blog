package kr.co.jiniaslog.shared.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig(
    private val redisConnectionFactory: RedisConnectionFactory
) {
    @Bean
    fun redisCacheManager(): RedisCacheManager {
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(1000))
                    .disableCachingNullValues()
                    .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
                    )
                    .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
                    )
            )
            .build()
    }
}
