package myblog.blog.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.dto.ArticleDtoForCardBox;
import myblog.blog.article.application.ArticleService;
import myblog.blog.shared.queries.LayoutRenderingQueries;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static myblog.blog.shared.utils.MarkdownUtils.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final ArticleService articleService;
    private final ModelMapper modelMapper;
    private final LayoutRenderingQueries layoutRenderingQueries;
    /*
        - 메인 화면 제어용 컨트롤러
    */
    @GetMapping("/")
    public String main(Model model) {
        // Dto 전처리
        List<ArticleDtoForCardBox> popularArticles = articleService.getPopularArticles()
                .stream()
                .map(article -> modelMapper.map(article, ArticleDtoForCardBox.class))
                .collect(Collectors.toList());
        //
        layoutRenderingQueries.AddLayoutTo(model);
        model.addAttribute("popularArticles", popularArticles);
        return "index";
    }

    /*
        - 최신 아티클 무한스크롤로 조회
    */
    @GetMapping("/main/article/{lastArticleId}")
    public @ResponseBody
    List<ArticleDtoForCardBox> mainNextPage(@PathVariable(required = false) Long lastArticleId) {

        // Entity to Dto
        List<ArticleDtoForCardBox> articles = articleService.getRecentArticles(lastArticleId)
                .stream()
                .map(article -> modelMapper.map(article, ArticleDtoForCardBox.class))
                .collect(Collectors.toList());


        // 화면렌더링을 위한 파싱
        for(ArticleDtoForCardBox article : articles){
            String content = Jsoup.parse(getHtmlRenderer().render(getParser().parse(article.getContent()))).text();
            if(content.length()>300) {
                content = content.substring(0, 300);
            }
            article.setContent(content);
        }

        return articles;
    }
}
