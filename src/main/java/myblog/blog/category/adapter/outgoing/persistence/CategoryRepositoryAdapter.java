package myblog.blog.category.adapter.outgoing.persistence;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.appliacation.port.outgoing.CategoryRepositoryPort;
import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.category.domain.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final MybatisCategoryRepository mybatisCategoryRepository;


    @Override
    public Optional<Category> findByTitle(String title) {
        return jpaCategoryRepository.findByTitle(title);
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAll();
    }

    @Override
    public List<CategorySimpleDto> getCategoryCount() {
        return mybatisCategoryRepository.getCategoryCount();
    }

    @Override
    public List<Category> findAllByTierIs(int tier) {
        return jpaCategoryRepository.findAllByTierIs(tier);
    }

    @Override
    public List<Category> findAllWithoutDummy() {
        return jpaCategoryRepository.findAllWithoutDummy();
    }

    @Override
    public void deleteAll(List<Category> categoryListFromDb) {
        jpaCategoryRepository.deleteAll(categoryListFromDb);
    }

    @Override
    public void save(Category category) {
        jpaCategoryRepository.save(category);
    }
}
