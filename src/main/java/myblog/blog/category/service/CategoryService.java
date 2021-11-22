package myblog.blog.category.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.category.domain.Category;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.repository.CategoryRepository;
import myblog.blog.category.repository.NaCategoryRepository;
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

    public Long createNewCategory(String title, String parent, int pOrder, int cOrder, int tier) {

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

        return category.getId();
    }

    public Category findCategory(String title) {
        return categoryRepository.findByTitle(title);
    }

    public List<CategoryNormalDto> getCategoryForView() {

        return naCategoryRepository.getCategoryCount();


    }

    public List<Category> findCategoryByTier(int tier) {
        return categoryRepository.findAllByTierIs(tier);
    }

    @Transactional
    public void changeCategory(List<CategoryNormalDto> categoryList) {

        sortingOrder(categoryList);

        List<Category> allWithoutDummy = categoryRepository.findAllWithoutDummy();

        while (!categoryList.isEmpty()) {

            CategoryNormalDto categoryNormalDto = categoryList.get(0);
            categoryList.remove(0);

            if (categoryNormalDto.getTier() == 1) {

                Category pCategory = null;
                if (categoryNormalDto.getId() == null) {
                    Long newCategoryId = createNewCategory(categoryNormalDto.getTitle(), null, categoryNormalDto.getPOrder(), categoryNormalDto.getCOrder(), categoryNormalDto.getTier());
                    pCategory = categoryRepository.findById(newCategoryId).get();

                } else {
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

                    Category cCategory = null;
                    if (subCategoryDto.getId() == null) {
                        Long newCategoryId = createNewCategory(subCategoryDto.getTitle(),
                                pCategory.getTitle(),
                                subCategoryDto.getPOrder(),
                                subCategoryDto.getCOrder(), subCategoryDto.getTier());
                        cCategory = categoryRepository.findById(newCategoryId).get();

                    } else {
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

        categoryRepository.deleteAll(allWithoutDummy);

    }

    private void sortingOrder(List<CategoryNormalDto> categoryList) {
        int pOrderIndex = 0;
        int cOrderIndex = 0;
        boolean isTier1 = false;
        for (CategoryNormalDto categoryNormalDto : categoryList) {

            System.out.println("categoryNormalDto = " + categoryNormalDto);

        }


        for (int i = 0; i < categoryList.size(); i++) {

            CategoryNormalDto categoryDto = categoryList.get(i);

            if (categoryDto.getTier() == 1) {
                cOrderIndex = 0;
                categoryDto.setPOrder(++pOrderIndex);
                categoryDto.setCOrder(cOrderIndex);
            } else {
                categoryDto.setPOrder(pOrderIndex);
                categoryDto.setCOrder(++cOrderIndex);
            }

        }

        for (CategoryNormalDto categoryNormalDto : categoryList) {

            System.out.println("categoryNormalDto = " + categoryNormalDto);

        }


    }


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
