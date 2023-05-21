package kr.co.jiniaslog.shared.core.infra

import java.time.Duration

interface CacheClient {
    fun deleteByKey(key: String)
    fun <T> find(key: String, clazz: Class<T>): T?
    fun <T> save(key: String, value: T, duration: Duration)
    fun <K, V> getMap(key: String): MutableMap<K, V>
    fun <K, V> saveInMap(mapKey: String, key: K, value: V)
    fun <K, V> findInMap(mapKey: String, key: K): V?
    fun <K, V> deleteInMap(mapKey: String, key: K)
    fun <K, V> findAllInMap(mapKey: String): Collection<V>
}
