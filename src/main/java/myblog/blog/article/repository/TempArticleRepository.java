package myblog.blog.article.repository;

import myblog.blog.article.domain.TempArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempArticleRepository extends JpaRepository<TempArticle, Long> {
}
