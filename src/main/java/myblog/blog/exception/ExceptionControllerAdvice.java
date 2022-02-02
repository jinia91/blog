package myblog.blog.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;

/*
    - 일반 에러 컨트롤러
*/
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler
    public String handleRuntimeException(Principal principal, HttpServletRequest req, RuntimeException e) {
        if (principal != null) {
            log.info("[{}]'{}' requested '{}'", LocalDateTime.now(),principal.getName(), req.getRequestURI());
        } else {
            log.info("[{}]requested '{}'",LocalDateTime.now(), req.getRequestURI());
        }
        log.error("[{}] but throw Exception, {}",LocalDateTime.now() ,e.getMessage());
        return "redirect:/error";
    }
}
