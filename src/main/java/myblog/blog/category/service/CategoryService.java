package myblog.blog.category.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.domain.Category;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.repository.CategoryRepository;
import myblog.blog.category.repository.NaCategoryRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

        categoryRepository.save(category);

        return category.getId();
    }

    public Category findCategory(String title) {
        return categoryRepository.findByTitle(title);
    }

    public CategoryForView getCategoryForView() {

        List<CategoryNormalDto> categoryCount = naCategoryRepository.getCategoryCount();

        return CategoryForView.createCategory(categoryCount);

    }

    public List<Category> findCategoryByTier(int tier) {
        return categoryRepository.findAllByTierIs(tier);
    }

    @PostConstruct
    public void insertCategory() {
        Category category1 = Category.builder()
                .tier(1)
                .title("카테고리 부모")
                .build();
        categoryRepository.save(category1);
        Category category2 = Category.builder()
                .tier(2)
                .title("카테고리 자식")
                .parents(category1)
                .build();
        categoryRepository.save(category2);

    }

}
