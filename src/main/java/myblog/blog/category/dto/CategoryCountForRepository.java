package myblog.blog.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCountForRepository {

    private String title;
    private int tier;
    private int count;

}
