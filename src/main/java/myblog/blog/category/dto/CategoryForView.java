package myblog.blog.category.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    - 레이아웃용 트리구조 카테고리 리스트
*/
@Getter
@Setter
public class CategoryForView {

    private int count;
    private String title;
    private Long id;
    private int pOrder;
    private int cOrder;
    // 트리구조를 갖기 위한 리스트
    private List<CategoryForView> categoryTCountList = new ArrayList<>();

    /*
        - 스태틱 생성 메서드
    */
    public static CategoryForView createCategory(List<CategoryNormalDto> crList) {
        return recursiveBuildFromCategoryDto(0, crList);
    }

    /*
    - 재귀호출로 트리구조 생성
        1. DTO객체 생성후 소스를 큐처리로 순차적 매핑
        2. Depth 변화시 재귀 호출 / 재귀 탈출
        3. 탈출시 상위 카테고리 list로 삽입하여 트리구조 작성
    */
    private static CategoryForView recursiveBuildFromCategoryDto(int tier, List<CategoryNormalDto> source) {

        CategoryForView categoryForView = new CategoryForView();

        while (!source.isEmpty()) {
            CategoryNormalDto cSource = source.get(0);

            if (cSource.getTier() == tier) {
                if(categoryForView.getTitle() != null
                        && !categoryForView.getTitle().equals(cSource.getTitle())){
                    return categoryForView;
                }
                categoryForView.setTitle(cSource.getTitle());
                categoryForView.setCount(cSource.getCount());
                categoryForView.setId(cSource.getId());
                categoryForView.setCOrder(cSource.getCOrder());
                categoryForView.setPOrder(cSource.getPOrder());
                source.remove(0);
            } else if (cSource.getTier() > tier) {
                CategoryForView sub = recursiveBuildFromCategoryDto(tier + 1, source);
                categoryForView.getCategoryTCountList().add(sub);
            } else {
                return categoryForView;
            }
        }
        return categoryForView;
    }

    private CategoryForView() {
    }
}
