package myblog.blog.article.application.port.incomming.response;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;

import static myblog.blog.shared.utils.MarkdownUtils.getHtmlRenderer;
import static myblog.blog.shared.utils.MarkdownUtils.getParser;

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

    public void parseAndRenderForView(){
        this.content = Jsoup.parse(getHtmlRenderer().render(getParser().parse(this.content))).text();
    }

}
