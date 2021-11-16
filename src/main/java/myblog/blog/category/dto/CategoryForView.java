package myblog.blog.category.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class CategoryForView {

    private int count;
    private String title;
    private int id;
    private List<CategoryForView> categoryTCountList = new ArrayList<>();

    public static CategoryForView createCategory(List<CategoryNormalDto> crList) {

        Collections.reverse(crList);
        return recursiveBuildFromCategoryDto(0, crList);

    }

    private CategoryForView() {
    }
    private static CategoryForView recursiveBuildFromCategoryDto(int d, List<CategoryNormalDto> crList) {

        CategoryForView categoryForView = new CategoryForView();

        while (!crList.isEmpty()) {
            CategoryNormalDto cSource = crList.get(0);

            if (cSource.getTier() == d) {
                if(categoryForView.getTitle() != null
                        && !categoryForView.getTitle().equals(cSource.getTitle())){
                    return categoryForView;
                }
                categoryForView.setTitle(cSource.getTitle());
                categoryForView.setCount(cSource.getCount());
                categoryForView.setId(cSource.getId());
                crList.remove(0);
            } else if (cSource.getTier() > d) {
                CategoryForView sub = recursiveBuildFromCategoryDto(d + 1, crList);
                categoryForView.getCategoryTCountList().add(sub);
            } else {
                return categoryForView;
            }

        }
        return categoryForView;
    }


}
