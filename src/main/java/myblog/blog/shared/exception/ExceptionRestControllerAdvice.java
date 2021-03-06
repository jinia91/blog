package myblog.blog.shared.exception;

import myblog.blog.comment.adapter.incomming.CommentBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
    - REST 컨트롤러 요청 에러 제어
*/
@RestControllerAdvice
public class ExceptionRestControllerAdvice {
    @ExceptionHandler(CommentBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCategoryControllerException(RuntimeException e) {
        return e.getMessage();
    }
}
