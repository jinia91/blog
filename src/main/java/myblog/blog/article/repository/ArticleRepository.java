package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop6ByOrderByHitDesc();

    Slice<Article> findByOrderByCreatedDateDesc(Pageable pageable);


}
