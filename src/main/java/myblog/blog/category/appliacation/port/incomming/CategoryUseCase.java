package myblog.blog.category.appliacation.port.incomming;

import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import myblog.blog.category.domain.Category;

import java.util.List;

public interface CategoryUseCase {
    Category findCategory(String title);
    List<Category> getAllCategories();
    void changeCategory(List<CategorySimpleDto> categoryList);
}
