package myblog.blog.category.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.domain.Category;
import myblog.blog.category.dto.CategoryCountForRepository;
import myblog.blog.category.dto.CategoryForMainView;
import myblog.blog.category.repository.CategoryRepository;
import myblog.blog.category.repository.NaCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final NaCategoryRepository naCategoryRepository;

    public Long createNewCategory(String title, String parent) {

        Category parentCategory = null;
        if (parent != null) {
            parentCategory = categoryRepository.findByTitle(parent);
        }

        Category category = Category.builder()
                .title(title)
                .parents(parentCategory)
                .build();

        return category.getId();
    }

    public Category findCategory(String title){
        return categoryRepository.findByTitle(title);
    }

    public CategoryForMainView getCategoryForView(){

        List<CategoryCountForRepository> categoryCount = naCategoryRepository.getCategoryCount();

        return CategoryForMainView.createCategory(categoryCount);

    }

}
