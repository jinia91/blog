package myblog.blog.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.dto.ArticleDtoForMain;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForLayout;
import myblog.blog.comment.service.CommentService;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final HtmlRenderer htmlRenderer;
    private final Parser parser;

    /*
        - 메인 화면 제어용 컨트롤러
    */
    @GetMapping("/")
    public String main(Model model) {

        // Dto 전처리
        CategoryForView categoryForView = categoryService.getCategoryForView();

        List<CommentDtoForLayout> comments = commentService.recentCommentList();

        List<ArticleDtoForMain> popularArticles = articleService.getPopularArticles();
        Slice<ArticleDtoForMain> recentArticles = articleService.getRecentArticles(0);

        for(ArticleDtoForMain article : recentArticles){
            article.setContent(Jsoup.parse(htmlRenderer.render(parser.parse(article.getContent()))).text());
        }

        //

        model.addAttribute("category",categoryForView);
        model.addAttribute("commentsList", comments);
        model.addAttribute("popularArticles", popularArticles);
        model.addAttribute("recentArticles",recentArticles);

        return "index";
    }

    /*
        - 최신 아티클 무한스크롤로 조회
    */
    @GetMapping("/main/article/{pageNum}")
    public @ResponseBody
    List<ArticleDtoForMain> mainNextPage(@PathVariable int pageNum) {


        List<ArticleDtoForMain> articles = articleService.getRecentArticles(pageNum).getContent();

        for(ArticleDtoForMain article : articles){
            String content = Jsoup.parse(htmlRenderer.render(parser.parse(article.getContent()))).text();
            if(content.length()>300) {
                content = content.substring(0, 300);
            }
            article.setContent(content);
        }

        return articles;
    }

}
