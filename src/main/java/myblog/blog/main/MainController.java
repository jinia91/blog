package myblog.blog.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.dto.ArticleDtoForMain;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForSide;
import myblog.blog.comment.service.CommentService;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    @GetMapping("/")
    public String main(Model model) {

        CategoryForView categoryForView = categoryService.getCategoryForView();

        model.addAttribute("category",categoryForView);
        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent()))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);

        List<ArticleDtoForMain> popularArticles = articleService.getPopularArticles();
        model.addAttribute("popularArticles", popularArticles);

        Slice<ArticleDtoForMain> recentArticles = articleService.getArticles(0);
        model.addAttribute("recentArticles",recentArticles);

        return "index";

    }



}
