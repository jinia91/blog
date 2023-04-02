package kr.co.jiniaslog.article.application.port

import java.util.concurrent.TimeUnit

interface CacheClient {
    fun deleteByKey(key: String?)
    fun expireByKey(key: String?, timeout: Long, timeUnit: TimeUnit?)
    fun <T> findFromString(key: String?, clazz: Class<T>?): T
}
