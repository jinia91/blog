package kr.co.jiniaslog.shared.core.infra

import java.time.Duration

interface CacheClient {
    fun deleteByKey(key: String)
    fun <T> find(key: String, clazz: Class<T>): T?
    fun <T> save(key: String, value: T, duration: Duration)
}
