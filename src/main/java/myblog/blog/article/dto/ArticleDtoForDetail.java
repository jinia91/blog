package myblog.blog.article.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter @Setter
public class ArticleDtoForDetail {

    private Long id;
    private String title;
    private String content;
    private String toc;
    private Long memberId;
    private String thumbnailUrl;
    private String category;
    private String tags;
    private LocalDateTime createdDate;


}
