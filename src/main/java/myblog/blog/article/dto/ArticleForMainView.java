package myblog.blog.article.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleForMainView {

    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private LocalDateTime createdDate;

}
