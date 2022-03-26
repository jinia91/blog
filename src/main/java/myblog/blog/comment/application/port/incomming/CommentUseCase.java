package myblog.blog.comment.application.port.incomming;

import myblog.blog.member.doamin.Member;

import java.util.List;

public interface CommentUseCase {
    List<CommentDto> getCommentList(Long articleId);
    void savePComment(String content, boolean secret, Member member, Long articleId);
    void saveCComment(String content, boolean secret, Member member, Long articleId, Long parentId);
    void deleteComment(Long commentId);
    List<CommentDtoForLayout> recentCommentList();
}
