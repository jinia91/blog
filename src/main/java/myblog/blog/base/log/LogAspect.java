package myblog.blog.base.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"local","dev"})
public class LogAspect {

    private final LogTracer logTracer;

    @Around("execution(* myblog.blog..*Controller.*(..))||execution(* myblog.blog..*Queries.*(..))||execution(* myblog.blog..*Service.*(..))||execution(* myblog.blog..*Repository.*(..))")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatusVO status = null;
        boolean hasException = false;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientIP = IpGetHelper.getClientIP(request);
        try {
            status = logTracer.begin(joinPoint.getSignature().toString(), Arrays.deepToString(joinPoint.getArgs()), clientIP);
            return joinPoint.proceed();
        } catch (Exception ex) {
            logTracer.exception(status, ex);
            hasException = true;
            throw ex;
        } finally {
            if(!hasException) logTracer.end(status);
        }
    }
}
