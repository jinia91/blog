package myblog.blog.category.adapter.incomming;
/*
    - REST 컨트롤러 상태 메세지 전송용 커스텀 에러
*/
public class InvalidCategoryRequestException extends RuntimeException {
    public InvalidCategoryRequestException(String message) {
        super(message);
    }
}
