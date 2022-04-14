package myblog.blog.category.appliacation.port.incomming.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/*
    - 범용 카테고리 DTO
*/
@Getter
@Setter
@ToString
public class CategorySimpleDto implements Cloneable {

    private Long id;
    @NotBlank(message = "카테고리명은 공백일 수 없습니다.")
    private String title;
    private int tier;
    private int count;
    private int pOrder;
    private int cOrder;

    @Override
    public CategorySimpleDto clone() {
        try {
            return (CategorySimpleDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    /*
        - 카테고리 변경을 위해 카테고리의 순번을 작성하는 로직
    */
    static public void sortByOrder(List<CategorySimpleDto> categoryList) {
        int pOrderIndex = 0;
        int cOrderIndex = 0;

        //티어별 트리구조로 순서 작성 로직
        for (CategorySimpleDto categorySimpleDto : categoryList) {
            if (categorySimpleDto.getTier() == 1) {
                cOrderIndex = 0;
                categorySimpleDto.setPOrder(++pOrderIndex);
                categorySimpleDto.setCOrder(cOrderIndex);
            } else {
                categorySimpleDto.setPOrder(pOrderIndex);
                categorySimpleDto.setCOrder(++cOrderIndex);
            }
        }
    }
    public boolean isSuperCategory(){
        return tier == 1;
    }
    public boolean isNewCategory(){
        return id == null;
    }
}
