package myblog.blog.comment.adapter.outgoing.persistence;

import lombok.RequiredArgsConstructor;
import myblog.blog.comment.application.port.outgoing.CommentRepositoryPort;
import myblog.blog.article.domain.Article;
import myblog.blog.comment.domain.Comment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepositoryPort {
    private final JpaCommentRepository jpaCommentRepository;
    private final MybatisCommentRepository mybatisCommentRepository;


    @Override
    public int countCommentsByArticleAndTier(Article article, int tier) {
        return jpaCommentRepository.countCommentsByArticleAndTier(article, tier);
    }

    @Override
    public void save(Comment comment) {
        jpaCommentRepository.save(comment);
    }

    @Override
    public List<Comment> findCommentsByArticleId(Long articleId) {
        return jpaCommentRepository.findCommentsByArticleId(articleId);
    }

    @Override
    public void deleteComment(Long commentId) {
        mybatisCommentRepository.deleteComment(commentId);
    }

    @Override
    public List<Comment> findTop5ByOrderByIdDesc() {
        return jpaCommentRepository.findTop5ByOrderByIdDesc();
    }

    @Override
    public Optional<Comment> findById(Long parentId) {
        return jpaCommentRepository.findById(parentId);
    }
}
