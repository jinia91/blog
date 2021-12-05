package myblog.blog.article.repository;

import myblog.blog.article.domain.TempArticle;
import org.springframework.data.jpa.repository.JpaRepository;

// 기본 JPA 메소드 사용
public interface TempArticleRepository extends JpaRepository<TempArticle, Long> {
}
