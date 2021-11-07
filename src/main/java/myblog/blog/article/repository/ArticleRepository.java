package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticleRepository extends JpaRepository<Article, Long> {



}
