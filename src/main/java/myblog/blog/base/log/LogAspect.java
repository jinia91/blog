package myblog.blog.base.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class LogAspect {

    private final LogTracer logTracer;

    @Around("execution(* myblog.blog..*Controller.*(..))||execution(* myblog.blog..*Queries.*(..))||execution(* myblog.blog..*Service.*(..))||execution(* myblog.blog..*Repository.*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatusVO status = null;
        boolean hasException = false;
        try {
            status = logTracer.begin("Return:"+joinPoint.getSignature().toString(), Arrays.deepToString(joinPoint.getArgs()));
            return joinPoint.proceed();
        } catch (Exception ex) {
            logTracer.handleException(status, ex);
            hasException = true;
            throw ex;
        } finally {
            if(!hasException) logTracer.end(status);
        }
    }
}
