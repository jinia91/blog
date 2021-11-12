package myblog.blog.category.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class CategoryForMainView {

    private int count;
    private String title;
    private int id;
    private List<CategoryForMainView> categoryTCountList = new ArrayList<>();

    public static CategoryForMainView createCategory(List<CategoryNormalDto> crList) {

        Collections.reverse(crList);
        return recursBuilding(0, crList);

    }

    private CategoryForMainView() {
    }
    private static CategoryForMainView recursBuilding(int d, List<CategoryNormalDto> crList) {

        CategoryForMainView categoryForMainView = new CategoryForMainView();

        while (!crList.isEmpty()) {
            CategoryNormalDto cSource = crList.get(0);

            if (cSource.getTier() == d) {
                if(categoryForMainView.getTitle() != null
                        && !categoryForMainView.getTitle().equals(cSource.getTitle())){
                    return categoryForMainView;
                }
                categoryForMainView.setTitle(cSource.getTitle());
                categoryForMainView.setCount(cSource.getCount());
                categoryForMainView.setId(cSource.getId());
                crList.remove(0);
            } else if (cSource.getTier() > d) {
                CategoryForMainView sub = recursBuilding(d + 1, crList);
                categoryForMainView.getCategoryTCountList().add(sub);
            } else {
                return categoryForMainView;
            }

        }
        return categoryForMainView;
    }


}
