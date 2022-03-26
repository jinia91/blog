package myblog.blog.article.application.port.incomming.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/*
    - 아티클 상세조회용 DTO
*/
@Getter @Setter
public class ArticleResponseForDetail {

    private Long id;
    private String title;
    private String content;
    private String toc;
    private Long memberId;
    private String thumbnailUrl;
    private String category;
    private List<String> tags;
    private LocalDateTime createdDate;
}
