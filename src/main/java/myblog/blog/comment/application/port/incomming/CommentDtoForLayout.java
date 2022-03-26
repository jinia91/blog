package myblog.blog.comment.application.port.incomming;

import lombok.Getter;
import lombok.Setter;
import myblog.blog.article.domain.Article;


/*
    - 레이아웃 노출용 댓글 DTO
*/
@Getter @Setter
public class CommentDtoForLayout {
    private Long id;
    private Long articleId;
    private String content;
    private boolean secret;

    public CommentDtoForLayout(Long id, Long articleId, String content, boolean secret) {
        this.id = id;
        this.secret = secret;
        this.articleId = articleId;
        this.content = content;
    }
}
