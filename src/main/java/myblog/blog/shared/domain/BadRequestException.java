package myblog.blog.shared.domain;


import org.springframework.http.HttpStatus;

// 400 - 요청 문법상 오류
public class BadRequestException extends BusinessException{

    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    // for rest
    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.BAD_REQUEST;
    };
    // for monitoring
    @Override
    public boolean isNecessaryToLog(){
        return false;
    };
}
