package myblog.blog.category.repository;

import myblog.blog.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByTitle(String title);
    List<Category> findAllByTierIs(int tier);


    @Query("select c " +
            "from Category c " +
            "where c.id >0 ")
    List<Category> findAllWithoutDummy();

}
