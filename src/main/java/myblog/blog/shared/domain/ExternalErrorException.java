package myblog.blog.shared.domain;


import org.springframework.http.HttpStatus;

// 인지된 500대 에러
public class ExternalErrorException extends BusinessException{

    public ExternalErrorException() {
    }

    public ExternalErrorException(String message) {
        super(message);
    }

    // for rest
    @Override
    public HttpStatus getHttpStatus(){
        return HttpStatus.INTERNAL_SERVER_ERROR;
    };
    // for monitoring
    @Override
    public boolean isNecessaryToLog(){
        return true;
    };
}
