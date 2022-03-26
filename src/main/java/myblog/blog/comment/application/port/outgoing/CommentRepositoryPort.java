package myblog.blog.comment.application.port.outgoing;

import myblog.blog.article.domain.Article;
import myblog.blog.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {
    int countCommentsByArticleAndTier(Article article, int tier);
    void save(Comment comment);
    List<Comment> findCommentsByArticleId(Long articleId);
    void deleteComment(Long commentId);
    List<Comment> findTop5ByOrderByIdDesc();
    Optional<Comment> findById(Long parentId);
}
