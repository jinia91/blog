package myblog.blog.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.dto.ArticleDtoForMain;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForLayout;
import myblog.blog.comment.service.CommentService;
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
    private final ModelMapper modelMapper;

    /*
        - 메인 화면 제어용 컨트롤러
    */
    @GetMapping("/")
    public String main(Model model) {

        // Dto 전처리
        CategoryForView categoryForView = CategoryForView.createCategory(categoryService.getCategoryForView());

        List<CommentDtoForLayout> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForLayout(comment.getId(), comment.getArticle().getId(), comment.getContent(),comment.isSecret()))
                .collect(Collectors.toList());

        List<ArticleDtoForMain> popularArticles = articleService.getPopularArticles()
                .stream()
                .map(article -> modelMapper.map(article, ArticleDtoForMain.class))
                .collect(Collectors.toList());

        Slice<ArticleDtoForMain> recentArticles = articleService.getRecentArticles(0)
                .map(article -> modelMapper.map(article, ArticleDtoForMain.class));
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

        return articleService.getRecentArticles(pageNum).getContent()
                .stream()
                .map(article -> modelMapper.map(article, ArticleDtoForMain.class))
                .collect(Collectors.toList());
    }

}
