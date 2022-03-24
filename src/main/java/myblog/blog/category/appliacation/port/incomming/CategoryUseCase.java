package myblog.blog.category.appliacation.port.incomming;

import myblog.blog.category.appliacation.port.response.CategorySimpleDto;
import myblog.blog.category.appliacation.port.response.CategoryViewForLayout;
import myblog.blog.category.domain.Category;

import java.util.List;

public interface CategoryUseCase {
    Category findCategory(String title);
    List<Category> getAllCategories();
    List<CategorySimpleDto> getCategorytCountList();
    CategoryViewForLayout getCategoryViewForLayout();
    List<CategorySimpleDto> findCategoryByTier(int tier);
    void changeCategory(List<CategorySimpleDto> categoryList);
}
