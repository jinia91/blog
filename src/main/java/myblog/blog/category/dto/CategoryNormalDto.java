package myblog.blog.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryNormalDto {

    private int id;
    private String title;
    private int tier;
    private int count;

}
