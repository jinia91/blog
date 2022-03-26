package myblog.blog.category.appliacation.port.incomming.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
    - 레이아웃용 트리구조 카테고리 리스트
*/
@Getter
@Setter
public class CategoryViewForLayout {

    private int count;
    private String title;
    private Long id;
    private int pOrder;
    private int cOrder;
    // 트리구조를 갖기 위한 리스트
    private List<CategoryViewForLayout> categoryTCountList = new ArrayList<>();

    /*
        - 스태틱 생성 메서드
    */
    public static CategoryViewForLayout from(List<CategorySimpleDto> crList) {
        return recursiveBuildFromCategoryDto(0, crList);
    }

    /*
    - 재귀호출로 트리구조 생성
        1. DTO객체 생성후 소스를 큐처리로 순차적 매핑
        2. Depth 변화시 재귀 호출 / 재귀 탈출
        3. 탈출시 상위 카테고리 list로 삽입하여 트리구조 작성
    */
    private static CategoryViewForLayout recursiveBuildFromCategoryDto(int tier, List<CategorySimpleDto> source) {

        CategoryViewForLayout categoryViewForLayout = new CategoryViewForLayout();

        while (!source.isEmpty()) {
            CategorySimpleDto cSource = source.get(0);

            if (cSource.getTier() == tier) {
                if(categoryViewForLayout.getTitle() != null
                        && !categoryViewForLayout.getTitle().equals(cSource.getTitle())){
                    return categoryViewForLayout;
                }
                categoryViewForLayout.setTitle(cSource.getTitle());
                categoryViewForLayout.setCount(cSource.getCount());
                categoryViewForLayout.setId(cSource.getId());
                categoryViewForLayout.setCOrder(cSource.getCOrder());
                categoryViewForLayout.setPOrder(cSource.getPOrder());
                source.remove(0);
            } else if (cSource.getTier() > tier) {
                CategoryViewForLayout sub = recursiveBuildFromCategoryDto(tier + 1, source);
                categoryViewForLayout.getCategoryTCountList().add(sub);
            } else {
                return categoryViewForLayout;
            }
        }
        return categoryViewForLayout;
    }

    private CategoryViewForLayout() {
    }
}
