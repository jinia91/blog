package myblog.blog.comment.adapter.incomming;
/*
    - REST 컨트롤러 상태 메세지 전송용 커스텀 에러
*/
public class InvalidCommentRequestException extends RuntimeException {
    public InvalidCommentRequestException(String message) {
        super(message);
    }
}
