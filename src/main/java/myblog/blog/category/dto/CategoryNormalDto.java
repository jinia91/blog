package myblog.blog.category.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryNormalDto {

    private Long id;
    private String title;
    private int tier;
    private int count;
    private int pOrder;
    private int cOrder;

}
