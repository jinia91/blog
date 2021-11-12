package myblog.blog.category.repository;

import myblog.blog.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByTitle(String title);
    List<Category> findAllByTierIs(int tier);

}
