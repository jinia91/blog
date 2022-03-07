package myblog.blog.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
    - REST 컨트롤러 요청 에러 제어
*/
@RestControllerAdvice
public class ExceptionRestControllerAdvice {
    @ExceptionHandler(CustomFormException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCategoryControllerException(RuntimeException e) {
        return e.getMessage();
    }
}
