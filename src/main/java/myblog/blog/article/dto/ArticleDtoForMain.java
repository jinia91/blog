package myblog.blog.article.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/*
    - 메인 화면 출력용 아티클 DTO
*/
@Getter
@Setter
public class ArticleDtoForMain {

    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private LocalDateTime createdDate;

}
