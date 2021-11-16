package myblog.blog.comment.repository;

import myblog.blog.article.domain.Article;
import myblog.blog.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c " +
            "from Comment c " +
            "left join fetch c.member " +
            "left join fetch c.parents " +
            "join c.article a " +
            "where a.id =:articleId " +
            "order by c.pOrder, c.id asc")
    List<Comment> findCommentsByArticleId(@Param("articleId") Long articleId);

    int countCommentsByArticleAndTier(Article article,int tier);
}
