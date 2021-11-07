package myblog.blog.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j
public class ExceptionController {

//    @ExceptionHandler
//    public String handleRuntimeException(Principal principal, HttpServletRequest req, RuntimeException e) {
//        if (principal != null) {
//            log.info("'{}' requested '{}' ", principal.getName(), req.getRequestURI());
//        } else {
//            log.info("requested '{}'", req.getRequestURI());
//        }
//
//        log.error("bad request", e);
//        return "";
//    }


}
