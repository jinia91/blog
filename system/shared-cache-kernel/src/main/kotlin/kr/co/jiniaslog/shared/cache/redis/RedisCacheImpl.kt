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

    override fun <K, V> getMap(key: String): MutableMap<K, V> {
        return redisClient.getMap(key)
    }

    override fun <K, V> saveInMap(mapKey: String, key: K, value: V) {
        getMap<K, V>(mapKey)[key] = value
    }

    override fun <K, V> findInMap(mapKey: String, key: K): V? {
        return getMap<K, V>(mapKey)[key]
    }

    override fun <K, V> deleteInMap(mapKey: String, key: K) {
        getMap<K, V>(mapKey).remove(key)
    }

    override fun <K, V> findAllInMap(mapKey: String): Collection<V> {
        return getMap<K, V>(mapKey).values
    }
}
