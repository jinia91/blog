package kr.co.jiniaslog.shared.config

import io.opentelemetry.api.trace.Tracer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class TracingAspect(
    private val tracer: Tracer
) {

    @Around(
        "@within(kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor) || " +
            "@annotation(kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor) ||" +
            "@within(kr.co.jiniaslog.shared.core.annotation.CustomComponent) || " +
            "@annotation(kr.co.jiniaslog.shared.core.annotation.CustomComponent) ||" +
            "@within(kr.co.jiniaslog.shared.core.annotation.DomainService) || " +
            "@annotation(kr.co.jiniaslog.shared.core.annotation.DomainService) ||" +
            "@within(kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter) || " +
            "@annotation(kr.co.jiniaslog.shared.core.annotation.PersistenceAdapter)"
    )
    fun traceAnnotation(pjp: ProceedingJoinPoint): Any? {
        val methodName = pjp.signature.toLongString()
        val span = tracer.spanBuilder(methodName).startSpan()

        return try {
            span.makeCurrent().use {
                pjp.proceed()
            }
        } catch (ex: Throwable) {
            span.recordException(ex)
            throw ex
        } finally {
            span.end()
        }
    }
}
