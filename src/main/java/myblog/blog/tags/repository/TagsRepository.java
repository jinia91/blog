package myblog.blog.tags.repository;

import myblog.blog.tags.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagsRepository extends JpaRepository<Tags, Long> {

    Tags findByName(String name);

}
