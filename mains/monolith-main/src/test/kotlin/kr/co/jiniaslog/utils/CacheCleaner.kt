package kr.co.jiniaslog.utils

import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

@Component
class CacheCleaner(
    private val cacheManager: CacheManager
) {
    fun burst() {
        cacheManager.cacheNames.forEach {
            cacheManager.getCache(it)?.clear()
        }
    }
}
