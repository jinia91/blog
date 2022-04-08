package myblog.blog.article.application.port.incomming.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import myblog.blog.article.adapter.incomming.ArticleForm;

@Getter
@AllArgsConstructor
public class ArticleEditCommand {

    private Long articleId;
    private String title;
    private String content;
    private String toc;
    private String thumbnailUrl;
    private String categoryName;
    private String tags;

    static public ArticleEditCommand from(Long articleId, ArticleForm articleForm){
        return new ArticleEditCommand(articleId,
                articleForm.getTitle(),
                articleForm.getContent(),
                articleForm.getToc(),
                articleForm.getThumbnailUrl(),
                articleForm.getCategory(),
                articleForm.getTags());
    }

}
