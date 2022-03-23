package myblog.blog.category.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/*
    - 범용 카테고리 DTO
*/
@Getter
@Setter
@ToString
public class CategorySimpleView {

    private Long id;
    @NotBlank(message = "카테고리명은 공백일 수 없습니다.")
    private String title;
    private int tier;
    private int count;
    private int pOrder;
    private int cOrder;

}
