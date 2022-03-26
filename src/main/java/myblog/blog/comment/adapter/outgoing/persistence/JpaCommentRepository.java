package myblog.blog.comment.adapter.outgoing.persistence;

import myblog.blog.article.domain.Article;
import myblog.blog.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {

    /*
        - 특정 아티클에 해당하는 댓글 리스트 가져오기
    */
    @Query("select c " +
            "from Comment c " +
            "left join fetch c.member " +
            "left join fetch c.parents " +
            "join c.article a " +
            "where a.id =:articleId " +
            "order by c.pOrder, c.id asc")
    List<Comment> findCommentsByArticleId(@Param("articleId") Long articleId);

    /*
        - 특정 아티클의 부모 댓글 총 갯수
    */
    int countCommentsByArticleAndTier(Article article,int tier);

    /*
        - 전체 댓글중 최신 댓글 5개
    */
    List<Comment> findTop5ByOrderByIdDesc();
}
