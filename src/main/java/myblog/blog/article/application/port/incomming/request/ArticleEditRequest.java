package myblog.blog.article.application.port.incomming.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import myblog.blog.article.adapter.incomming.ArticleForm;

@Getter
@AllArgsConstructor
public class ArticleEditRequest {

    private Long articleId;
    private String title;
    private String content;
    private String toc;
    private String thumbnailUrl;
    private String categoryName;
    private String tags;

    static public ArticleEditRequest from(Long articleId, ArticleForm articleForm){
        return new ArticleEditRequest(articleId,
                articleForm.getTitle(),
                articleForm.getContent(),
                articleForm.getToc(),
                articleForm.getThumbnailUrl(),
                articleForm.getCategory(),
                articleForm.getTags());
    }

}
