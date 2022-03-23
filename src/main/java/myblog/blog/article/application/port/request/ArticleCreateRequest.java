package myblog.blog.article.application.port.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import myblog.blog.article.adapter.incomming.web.ArticleForm;

@Getter
@AllArgsConstructor
public class ArticleCreateRequest {
    private Long memberId;
    private String title;
    private String content;
    private String toc;
    private String thumbnailUrl;
    private String category;
    private String tags;

    static public ArticleCreateRequest from(ArticleForm articleForm, Long memberId){
        return new ArticleCreateRequest(memberId,
                articleForm.getTitle(),
                articleForm.getContent(),
                articleForm.getToc(),
                articleForm.getThumbnailUrl(),
                articleForm.getCategory(),
                articleForm.getTags());
    }
}
