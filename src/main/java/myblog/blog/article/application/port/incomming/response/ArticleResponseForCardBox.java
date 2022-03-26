package myblog.blog.article.application.port.incomming.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
    - 메인 화면 렌더링용 아티클 DTO
*/
@Getter
@Setter
public class ArticleResponseForCardBox {
    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private LocalDateTime createdDate;

}
