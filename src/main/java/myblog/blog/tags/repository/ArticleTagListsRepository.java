package myblog.blog.tags.repository;

import myblog.blog.tags.domain.ArticleTagList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleTagListsRepository extends JpaRepository<ArticleTagList, Long> {
}
