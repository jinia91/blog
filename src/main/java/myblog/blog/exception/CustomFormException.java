package myblog.blog.exception;
/*
    - REST 컨트롤러 상태 메세지 전송용 커스텀 에러
*/
public class CustomFormException extends RuntimeException {
    public CustomFormException(String message) {
        super(message);
    }
}
