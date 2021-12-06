package myblog.blog.article.dto;

import lombok.Getter;
import lombok.Setter;

/*
    - 카테고리별 게시물 표시용 DTO
*/
@Getter @Setter
public class ArticleDtoByCategory {

    private String title;
    private Long id;
}
