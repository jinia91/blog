package kr.co.jiniaslog.shared.messaging.events

import kr.co.jiniaslog.shared.core.infra.CacheClient
import org.springframework.integration.metadata.ConcurrentMetadataStore
import org.springframework.stereotype.Component
import java.time.Duration

@Component
internal class GlobalMetaDataStore(
    private val cacheClient: CacheClient,
) : ConcurrentMetadataStore {

    private val defaultDuration = Duration.ofMinutes(30)

    override fun put(key: String, value: String) {
        cacheClient.save(key, value, defaultDuration)
    }

    override fun get(key: String): String? {
        return cacheClient.find(key, String::class.java)
    }

    override fun remove(key: String): String? {
        val oldValue = cacheClient.find(key, String::class.java)
        cacheClient.deleteByKey(key)
        return oldValue
    }

    override fun putIfAbsent(key: String, value: String): String? {
        val existingValue = cacheClient.find(key, String::class.java)
        if (existingValue == null) {
            cacheClient.save(key, value, defaultDuration)
        }
        return existingValue
    }

    override fun replace(key: String, oldValue: String, newValue: String): Boolean {
        val existingValue = cacheClient.find(key, String::class.java)
        if (existingValue == oldValue) {
            cacheClient.save(key, newValue, defaultDuration)
            return true
        }
        return false
    }
}
