package kr.co.jiniaslog.annotation

import org.springframework.test.context.junit.jupiter.EnabledIf

/**
 * Ci 에서만 실행하는 테스트
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EnabledIf(expression = "#{environment['spring.profiles.active'] == 'ci'}", loadContext = true)
annotation class Ci(val reason: String = "")
