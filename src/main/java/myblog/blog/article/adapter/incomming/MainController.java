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
        var popularArticles = articleQueriesUseCase.getPopularArticles();
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("popularArticles", popularArticles);
        return "index";
    }

    /*
        - 최신 아티클 무한스크롤로 조회
    */
    @GetMapping("/main/article/{lastArticleId}")
    @ResponseBody List<ArticleResponseForCardBox> mainNextPage(@PathVariable(required = false) Long lastArticleId) {
        var articles = articleQueriesUseCase.getRecentArticles(lastArticleId);
        for(var article : articles) article.parseAndRenderForView();
        return articles;
    }

    /*
    *  - about me page
    * */
    @GetMapping("/aboutMe")
    String aboutMe(Model model) {
        layoutRenderingUseCase.AddLayoutTo(model);
        return "aboutMe";
    }
}
