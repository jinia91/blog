package myblog.blog.category.appliacation;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.appliacation.port.incomming.CategoryQueriesUseCase;
import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import myblog.blog.category.appliacation.port.outgoing.CategoryRepositoryPort;
import myblog.blog.shared.utils.MapperUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryQueries implements CategoryQueriesUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;

    /*
        - 카테고리와 카테고리별 아티클 수 찾기
    */
    @Override
    public List<CategorySimpleDto> getCategorytCountList() {
        return categoryRepositoryPort.getCategoryCount();
    }

    /*
        - getCategorytCountList()의 캐싱을 위한 전처리 매핑 로직
            - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
              카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
    */
    @Cacheable(value = "layoutCaching", key = "0")
    @Override
    public CategoryViewForLayout getCategoryViewForLayout() {
        return CategoryViewForLayout.from(categoryRepositoryPort.getCategoryCount());
    }

    /*
        - 티어별 카테고리 목록 찾기
    */
    @Override
    public List<CategorySimpleDto> findCategoryByTier(int tier) {
        return categoryRepositoryPort.findAllByTierIs(tier)
                .stream()
                .map(category -> MapperUtils.getModelMapper().map(category, CategorySimpleDto.class))
                .collect(Collectors.toList());
    }
}
