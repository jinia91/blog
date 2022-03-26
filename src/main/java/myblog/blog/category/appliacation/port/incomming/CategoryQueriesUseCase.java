package myblog.blog.category.appliacation.port.incomming;

import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface CategoryQueriesUseCase {
    List<CategorySimpleDto> getCategorytCountList();
    CategoryViewForLayout getCategoryViewForLayout();
    List<CategorySimpleDto> findCategoryByTier(int tier);
}
