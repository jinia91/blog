package myblog.blog.category.appliacation;

import lombok.RequiredArgsConstructor;

import myblog.blog.category.domain.Category;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.category.appliacation.port.outgoing.CategoryRepositoryPort;
import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;

import myblog.blog.category.domain.CategoryNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService implements CategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;

    /*
        - 카테고리 이름으로 카테고리 찾기
    */
    @Override
    public Category findCategory(String title) {
        return categoryRepositoryPort.findByTitle(title)
                .orElseThrow(CategoryNotFoundException::new);
    }

    /*
        - 카테고리 이름으로 카테고리 찾기
    */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepositoryPort.findAll();
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
        var categoryListFromDb = categoryRepositoryPort.findAllWithoutDummy();
        // 3. 카테고리 변경
        while (!categoryList.isEmpty()) {
            var categorySimpleDto = categoryList.get(0);
            categoryList.remove(0);
            if (categorySimpleDto.isSuperCategory()) {
                Category pCategory = null;
                if (categorySimpleDto.isNewCategory()) pCategory = createNewCategory(categorySimpleDto, null);
                else {
                    pCategory = findMatchingCategory(categoryListFromDb, categorySimpleDto, pCategory);
                    pCategory.updateCategory(categorySimpleDto.getTitle(), categorySimpleDto.getTier(), categorySimpleDto.getPOrder(), categorySimpleDto.getCOrder(), null);
                }

                while (!categoryList.isEmpty()) {
                    var subCategorySimpleDto = categoryList.get(0);
                    if (subCategorySimpleDto.isSuperCategory()) break;
                    categoryList.remove(0);
                    Category cCategory = null;
                    if (subCategorySimpleDto.isNewCategory()) cCategory = createNewCategory(subCategorySimpleDto, pCategory.getTitle());
                    else {
                        cCategory = findMatchingCategory(categoryListFromDb, subCategorySimpleDto, cCategory);
                        cCategory.updateCategory(subCategorySimpleDto.getTitle(), subCategorySimpleDto.getTier(), subCategorySimpleDto.getPOrder(), subCategorySimpleDto.getCOrder(), pCategory);
                    }
                }
            }
        }
        // 3-3 불일치 카테고리 전부 삭제
        categoryRepositoryPort.deleteAll(categoryListFromDb);
    }

    private Category findMatchingCategory(List<Category> categoryListFromDb, CategorySimpleDto categorySimpleDto, Category category) {
        for (int i = 0; i < categoryListFromDb.size(); i++) {
            if (categoryListFromDb.get(i).getId().equals(categorySimpleDto.getId())) {
                category = categoryListFromDb.get(i);
                categoryListFromDb.remove(i);
                break;
            }
        }
        return category;
    }

    /*
    - 새로운 카테고리 생성하기
        - 상위 카테고리 존재 유무 분기
    */
    private Category createNewCategory(CategorySimpleDto categorySimpleDto, String parent) {
        Category parentCategory = null;
        if (parent != null) {
            parentCategory = categoryRepositoryPort.findByTitle(parent)
                    .orElseThrow(CategoryNotFoundException::new);
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
