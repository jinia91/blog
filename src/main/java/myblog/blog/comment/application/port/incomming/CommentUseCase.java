package myblog.blog.comment.application.port.incomming;

import myblog.blog.comment.application.port.incomming.response.CommentDto;
import myblog.blog.comment.application.port.incomming.response.CommentDtoForLayout;

import java.util.List;

public interface CommentUseCase {
    void savePComment(String content, boolean secret, Long memberId, Long articleId);
    void saveCComment(String content, boolean secret, Long memberId, Long articleId, Long parentId);
    void deleteComment(Long commentId);
}
