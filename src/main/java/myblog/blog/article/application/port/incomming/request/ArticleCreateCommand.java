package myblog.blog.article.application.port.incomming.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import myblog.blog.article.adapter.incomming.ArticleForm;

@Getter
@AllArgsConstructor
public class ArticleCreateCommand {
    private Long memberId;
    private String title;
    private String content;
    private String toc;
    private String thumbnailUrl;
    private String category;
    private String tags;

    static public ArticleCreateCommand from(ArticleForm articleForm, Long memberId){
        return new ArticleCreateCommand(memberId,
                articleForm.getTitle(),
                articleForm.getContent(),
                articleForm.getToc(),
                articleForm.getThumbnailUrl(),
                articleForm.getCategory(),
                articleForm.getTags());
    }
}
