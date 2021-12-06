package myblog.blog.category.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.domain.Category;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.repository.CategoryRepository;
import myblog.blog.category.repository.NaCategoryRepository;
import myblog.blog.member.doamin.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final NaCategoryRepository naCategoryRepository;

    /*
        - 새로운 카테고리 생성하기
            - 상위 카테고리 존재 유무 분기
    */
    public Category createNewCategory(String title, String parent, int pOrder, int cOrder, int tier) {

        Category parentCategory = null;
        if (parent != null) {
            parentCategory = categoryRepository.findByTitle(parent);
        }

        Category category = Category.builder()
                .title(title)
                .pSortNum(pOrder)
                .cSortNum(cOrder)
                .tier(tier)
                .parents(parentCategory)
                .build();

        categoryRepository.save(category);

        return category;
    }

    /*
        - 카테고리 이름으로 카테고리 찾기
    */
    public Category findCategory(String title) {
        return categoryRepository.findByTitle(title);
    }

    /*
        - 카테고리와 카테고리별 아티클 수 찾기
    */
    public List<CategoryNormalDto> getCategoryForView() {

        return naCategoryRepository.getCategoryCount();

    }

    /*
        - 티어별 카테고리 목록 찾기
    */
    public List<Category> findCategoryByTier(int tier) {
        return categoryRepository.findAllByTierIs(tier);
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
    @Transactional
    public void changeCategory(List<CategoryNormalDto> categoryList) {

        // 1.카테고리 리스트 순서 작성
        sortingOrder(categoryList);

        // 2. 기존 DB 저장된 카테고리 리스트 불러오기
        List<Category> allWithoutDummy = categoryRepository.findAllWithoutDummy();

        // 3. 카테고리 변경 루프
        while (!categoryList.isEmpty()) {

            CategoryNormalDto categoryNormalDto = categoryList.get(0);
            categoryList.remove(0);

            // 부모카테고리인경우
            if (categoryNormalDto.getTier() == 1) {

                Category pCategory = null;

                // 부모카테고리가 기존에 존재 x
                if (categoryNormalDto.getId() == null) {
                    pCategory
                            = createNewCategory(categoryNormalDto.getTitle(),
                            null,
                            categoryNormalDto.getPOrder(),
                            categoryNormalDto.getCOrder(),
                            categoryNormalDto.getTier());

                }
                // 부모카테고리가 기존에 존재 o
                else {
                    for (int i = 0; i < allWithoutDummy.size(); i++) {
                        if (allWithoutDummy.get(i).getId().equals(categoryNormalDto.getId())) {
                            pCategory = allWithoutDummy.get(i);
                            allWithoutDummy.remove(i);
                            break;
                        }
                    }
                    pCategory.updateCategory(categoryNormalDto.getTitle(),
                            categoryNormalDto.getTier(),
                            categoryNormalDto.getPOrder(),
                            categoryNormalDto.getCOrder(),
                            null);
                }

                while (!categoryList.isEmpty()) {

                    CategoryNormalDto subCategoryDto = categoryList.get(0);
                    if (subCategoryDto.getTier() == 1) break;
                    categoryList.remove(0);

                    // 자식 카테고리인경우
                    Category cCategory = null;
                    // 카테고리가 기존에 존재 x
                    if (subCategoryDto.getId() == null) {
                        cCategory = createNewCategory(subCategoryDto.getTitle(),
                                pCategory.getTitle(),
                                subCategoryDto.getPOrder(),
                                subCategoryDto.getCOrder(),
                                subCategoryDto.getTier());

                    }
                    // 카테고리가 기존에 존재 o
                    else {
                        for (int i = 0; i < allWithoutDummy.size(); i++) {
                            if (allWithoutDummy.get(i).getId().equals(subCategoryDto.getId())) {
                                cCategory = allWithoutDummy.get(i);
                                allWithoutDummy.remove(i);
                                break;
                            }
                        }
                        cCategory.updateCategory(subCategoryDto.getTitle(),
                                subCategoryDto.getTier(),
                                subCategoryDto.getPOrder(),
                                subCategoryDto.getCOrder(),
                                pCategory);
                    }
                }
            }
        }
        // 3-3 불일치 카테고리 전부 삭제
        categoryRepository.deleteAll(allWithoutDummy);
    }

    /*
        - 카테고리 변경을 위해 카테고리의 순번을 작성하는 로직
    */
    private void sortingOrder(List<CategoryNormalDto> categoryList) {
        int pOrderIndex = 0;
        int cOrderIndex = 0;

        //티어별 트리구조로 순서 작성 로직
        for (CategoryNormalDto categoryDto : categoryList) {

            if (categoryDto.getTier() == 1) {
                cOrderIndex = 0;
                categoryDto.setPOrder(++pOrderIndex);
                categoryDto.setCOrder(cOrderIndex);
            } else {
                categoryDto.setPOrder(pOrderIndex);
                categoryDto.setCOrder(++cOrderIndex);
            }
        }
    }

    /*
        - 최초 더미 카테고리 추가 코드
    */
    @PostConstruct
    public void insertCategory() {

        Category category0 = Category.builder()
                .tier(0)
                .title("total")
                .pSortNum(0)
                .cSortNum(0)
                .build();
            categoryRepository.save(category0);

            Category category1 = Category.builder()
                .tier(1)
                .title("카테고리 부모")
                .pSortNum(1)
                .cSortNum(0)
                .build();
        categoryRepository.save(category1);
        Category category2 = Category.builder()
                .tier(2)
                .title("카테고리 자식")
                .pSortNum(1)
                .cSortNum(1)
                .parents(category1)
                .build();
        categoryRepository.save(category2);
        Category category3 = Category.builder()
                .tier(1)
                .title("카테고리 부모2")
                .pSortNum(2)
                .cSortNum(0)
                .build();
        categoryRepository.save(category3);
        Category category4 = Category.builder()
                .tier(1)
                .title("카테고리 부모3")
                .pSortNum(3)
                .cSortNum(0)
                .build();
        categoryRepository.save(category4);
        Category category5 = Category.builder()
                .tier(2)
                .title("카테고리 자식2")
                .pSortNum(2)
                .cSortNum(1)
                .parents(category3)
                .build();
        categoryRepository.save(category5);
        Category category6 = Category.builder()
                .tier(2)
                .title("카테고리 자식3")
                .pSortNum(2)
                .cSortNum(2)
                .parents(category3)
                .build();
        categoryRepository.save(category6);
        Category category7 = Category.builder()
                .tier(2)
                .title("카테고리 자식4")
                .pSortNum(3)
                .cSortNum(1)
                .parents(category4)
                .build();
        categoryRepository.save(category7);

    }

}
