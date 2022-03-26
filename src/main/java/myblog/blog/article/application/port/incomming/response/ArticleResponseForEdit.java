package myblog.blog.article.application.port.incomming.response;

import lombok.Getter;
import lombok.Setter;
import myblog.blog.category.domain.Category;

import java.util.ArrayList;
import java.util.List;

/*
    - 아티클 수정 폼을 위한 DTO
*/
@Getter @Setter
public class ArticleResponseForEdit {

    private Long id;
    private String title;
    private String content;
    private String toc;
    private String thumbnailUrl;

    private List<String> articleTagList = new ArrayList<>();
    private Category category;
}
