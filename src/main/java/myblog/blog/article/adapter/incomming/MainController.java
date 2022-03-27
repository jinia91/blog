package myblog.blog.article.adapter.incomming;

import myblog.blog.article.application.port.incomming.response.ArticleResponseForCardBox;
import myblog.blog.article.application.port.incomming.ArticleQueriesUseCase;
import myblog.blog.shared.application.port.incomming.LayoutRenderingUseCase;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static myblog.blog.shared.utils.MarkdownUtils.*;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ArticleQueriesUseCase articleQueriesUseCase;
    private final LayoutRenderingUseCase layoutRenderingUseCase;
    /*
        - 메인 화면 제어용 컨트롤러
    */
    @GetMapping("/")
    String main(Model model) {
        // Dto 전처리
        List<ArticleResponseForCardBox> popularArticles = articleQueriesUseCase.getPopularArticles();
        //
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("popularArticles", popularArticles);
        return "index";
    }

    /*
        - 최신 아티클 무한스크롤로 조회
    */
    @GetMapping("/main/article/{lastArticleId}")
    @ResponseBody List<ArticleResponseForCardBox> mainNextPage(@PathVariable(required = false) Long lastArticleId) {

        // Entity to Dto
        List<ArticleResponseForCardBox> articles = articleQueriesUseCase.getRecentArticles(lastArticleId);

        // 화면렌더링을 위한 파싱
        for(ArticleResponseForCardBox article : articles){
            String content = Jsoup.parse(getHtmlRenderer().render(getParser().parse(article.getContent()))).text();
            if(content.length()>300) {
                content = content.substring(0, 300);
            }
            article.setContent(content);
        }
        return articles;
    }
}
