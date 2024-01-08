package kr.co.jiniaslog.memo.adapter.inbound.http

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthUserId(val expression: String = "userId")
