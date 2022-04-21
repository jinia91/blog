package myblog.blog.shared.domain;


import org.springframework.http.HttpStatus;

// 403 접근권한없음
public class NotAuthorizedException extends BusinessException{

    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String message) {
        super(message);
    }

    // for rest
    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.FORBIDDEN;
    };
    // for monitoring
    @Override
    public boolean isNecessaryToLog(){
        return false;
    };
}
