package myblog.blog.article.adapter.outgoing.persistence;

import myblog.blog.article.domain.TempArticle;
import org.springframework.data.jpa.repository.JpaRepository;

// 기본 JPA 메소드 사용
public interface JpaTempArticleRepository extends JpaRepository<TempArticle, Long> {
}
