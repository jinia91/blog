package myblog.blog.shared.domain;


import org.springframework.http.HttpStatus;

// 비지니스상 개발자가 인지하는 예외 정의, 해당 예외 이외의 예외발생시는 긴급 모니터링 대상!
public abstract class BusinessException extends RuntimeException{

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    // for rest
    public abstract HttpStatus getHttpStatus();
    // for monitoring
    public abstract boolean isNecessaryToLog();
}
