package myblog.blog.article.dto;

import lombok.Getter;
import lombok.Setter;
import myblog.blog.article.domain.Article;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Setter
@Getter
public class ArticleForm {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String toc;

    private String thumbnailUrl;

    @NotBlank
    private String category;
    @NotBlank
    private String tags;

}
