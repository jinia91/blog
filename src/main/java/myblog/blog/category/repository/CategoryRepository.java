package myblog.blog.category.repository;

import myblog.blog.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /*
        - 카테고리 이름으로 카테고리 찾기
    */
    Category findByTitle(String title);

    /*
        - 티어별 카테고리들 가져오기
    */
    List<Category> findAllByTierIs(int tier);

    /*
        - ID == 0 인 더미카테고리를 제외한 모든 카테고리 불러오기
    */
    @Query("select c " +
            "from Category c " +
            "where c.id >0 ")
    List<Category> findAllWithoutDummy();

}
