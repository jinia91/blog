package myblog.blog.article.adapter.outgoing.persistence;

import myblog.blog.article.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaTagsRepository extends JpaRepository<Tags, Long> {
    Optional<Tags> findByName(String name);
}
