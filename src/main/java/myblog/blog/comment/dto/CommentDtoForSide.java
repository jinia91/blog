package myblog.blog.comment.dto;

import lombok.Getter;
import lombok.Setter;
import myblog.blog.article.domain.Article;

@Getter @Setter
public class CommentDtoForSide {
    private Long id;
    private Long articleId;
    private String content;

    public CommentDtoForSide(Long id, Long articleId, String content) {
        this.id = id;
        this.articleId = articleId;
        this.content = content;
    }
}
