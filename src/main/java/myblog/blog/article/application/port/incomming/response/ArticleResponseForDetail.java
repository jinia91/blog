package myblog.blog.article.application.port.incomming.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static myblog.blog.shared.utils.MarkdownUtils.getHtmlRenderer;
import static myblog.blog.shared.utils.MarkdownUtils.getParser;

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

    public void parseAndRenderForView(){
        this.content = getHtmlRenderer().render(getParser().parse(this.content));
    }
}
