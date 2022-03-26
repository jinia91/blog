package myblog.blog.category.appliacation.port.outgoing;

import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.category.domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Optional<Category> findByTitle(String title);
    List<Category> findAll();
    List<CategorySimpleDto> getCategoryCount();
    List<Category> findAllByTierIs(int tier);
    List<Category> findAllWithoutDummy();
    void deleteAll(List<Category> categoryListFromDb);
    void save(Category category);
}
