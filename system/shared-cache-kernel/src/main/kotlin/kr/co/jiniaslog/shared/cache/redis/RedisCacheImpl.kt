package kr.co.jiniaslog.shared.cache.redis

import kr.co.jiniaslog.shared.core.infra.CacheClient
import org.redisson.api.RBucket
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
internal class RedisCacheImpl(
    private val redisClient: RedissonClient,
) : CacheClient {

    override fun deleteByKey(key: String) {
        val bucket: RBucket<Any> = redisClient.getBucket(key)
        bucket.delete()
    }

    override fun <T> find(key: String, clazz: Class<T>): T? {
        val bucket: RBucket<T> = redisClient.getBucket(key)
        return bucket.get()
    }

    override fun <T> save(key: String, value: T, duration: Duration) {
        val bucket: RBucket<T> = redisClient.getBucket(key)
        bucket.set(value, duration.toMillis(), TimeUnit.MILLISECONDS)
    }
}
