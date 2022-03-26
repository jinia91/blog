package myblog.blog.comment.application.port.incomming;

import myblog.blog.comment.application.port.incomming.response.CommentDto;
import myblog.blog.comment.application.port.incomming.response.CommentDtoForLayout;

import java.util.List;

public interface CommentQueriesUseCase {
    List<CommentDto> getCommentList(Long articleId);
    List<CommentDtoForLayout> recentCommentListForLayout();
}
