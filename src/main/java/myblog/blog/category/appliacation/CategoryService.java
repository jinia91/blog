package myblog.blog.category.appliacation;

import lombok.RequiredArgsConstructor;

import myblog.blog.category.domain.Category;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.category.appliacation.port.outgoing.CategoryRepositoryPort;
import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService implements CategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final ModelMapper modelMapper;

    /*
        - 카테고리 이름으로 카테고리 찾기
    */
    @Override
    public Category findCategory(String title) {
        return categoryRepositoryPort.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("NotFoundCategoryException"));
    }

    /*
        - 카테고리 이름으로 카테고리 찾기
    */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepositoryPort.findAll();
    }

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
                .map(category -> modelMapper.map(category, CategorySimpleDto.class))
                .collect(Collectors.toList());
    }

    /*
        - 카테고리 변경 로직
            1. 카테고리 리스트의 순서 작성
            2. 입력받은 카테고리리스트와 DB의 전체카테고리 리스트 두개를 큐로 처리하여 비교대조
            3. 해당 카테고리 변경처리
                3-1 해당 카테고리 존재시 더티체킹으로 업데이트 처리
                3-2 DB에 존재하지 않는경우 새로 생성
                3-3 DB에만 존재하는 카테고리는 삭제처리
    */
    @Override
    @Transactional
    @CacheEvict(value = {"layoutCaching", "seoCaching"}, allEntries = true)
    public void changeCategory(List<CategorySimpleDto> categoryList) {

        // 1.카테고리 리스트 순서 작성
        CategorySimpleDto.sortByOrder(categoryList);
        // 2. 기존 DB 저장된 카테고리 리스트 불러오기
        List<Category> categoryListFromDb = categoryRepositoryPort.findAllWithoutDummy();

        // 3. 카테고리 변경
        while (!categoryList.isEmpty()) {
            CategorySimpleDto categorySimpleDto = categoryList.get(0);
            categoryList.remove(0);

            // 부모카테고리인경우
            if (categorySimpleDto.getTier() == 1) {
                Category pCategory = null;
                // 신규 부모인경우
                if (categorySimpleDto.getId() == null) {
                    pCategory = createNewCategory(categorySimpleDto, null);
                }
                // 기존 부모인경우
                else {
                    for (int i = 0; i < categoryListFromDb.size(); i++) {
                        if (categoryListFromDb.get(i).getId().equals(categorySimpleDto.getId())) {
                            pCategory = categoryListFromDb.get(i);
                            categoryListFromDb.remove(i);
                            break;
                        }
                    }
                    pCategory.updateCategory(
                            categorySimpleDto.getTitle(),
                            categorySimpleDto.getTier(),
                            categorySimpleDto.getPOrder(),
                            categorySimpleDto.getCOrder(),
                            null
                    );
                }

                while (!categoryList.isEmpty()) {

                    CategorySimpleDto subCategorySimpleDto = categoryList.get(0);
                    if (subCategorySimpleDto.getTier() == 1) break;
                    categoryList.remove(0);
                    // 자식 카테고리인경우
                    Category cCategory = null;
                    // 카테고리가 기존에 존재 x
                    if (subCategorySimpleDto.getId() == null) {
                        cCategory = createNewCategory(subCategorySimpleDto, pCategory.getTitle());
                    }
                    // 카테고리가 기존에 존재 o
                    else {
                        for (int i = 0; i < categoryListFromDb.size(); i++) {
                            if (categoryListFromDb.get(i).getId().equals(subCategorySimpleDto.getId())) {
                                cCategory = categoryListFromDb.get(i);
                                categoryListFromDb.remove(i);
                                break;
                            }
                        }
                        cCategory.updateCategory(
                                subCategorySimpleDto.getTitle(),
                                subCategorySimpleDto.getTier(),
                                subCategorySimpleDto.getPOrder(),
                                subCategorySimpleDto.getCOrder(),
                                pCategory);
                    }
                }
            }
        }
        // 3-3 불일치 카테고리 전부 삭제
        categoryRepositoryPort.deleteAll(categoryListFromDb);
    }
    /*
    - 새로운 카테고리 생성하기
        - 상위 카테고리 존재 유무 분기
    */
    private Category createNewCategory(CategorySimpleDto categorySimpleDto, String parent) {
        Category parentCategory = null;
        if (parent != null) {
            parentCategory = categoryRepositoryPort.findByTitle(parent)
                    .orElseThrow(() -> new IllegalArgumentException("NotFoundCategoryException"));
        }

        Category category = Category.builder()
                .title(categorySimpleDto.getTitle())
                .pSortNum(categorySimpleDto.getPOrder())
                .cSortNum(categorySimpleDto.getCOrder())
                .tier(categorySimpleDto.getTier())
                .parents(parentCategory)
                .build();
        categoryRepositoryPort.save(category);
        return category;
    }

//    /*
//        - 최초 필수 더미 카테고리 추가 코드
//    */
//    @PostConstruct
//    private void insertDummyCategory() {
//        if(categoryRepositoryPort.findByTitle("total")==null) {
//            Category category0 = Category.builder()
//                    .tier(0)
//                    .title("total")
//                    .pSortNum(0)
//                    .cSortNum(0)
//                    .build();
//            categoryRepositoryPort.save(category0);
//        }
//    }
}
