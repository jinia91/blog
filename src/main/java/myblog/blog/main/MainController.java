package myblog.blog.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.dto.ArticleDtoForMainView;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String main(Model model) {

        List<ArticleDtoForMainView> popularArticles = articleService.getPopularArticles();
        Slice<ArticleDtoForMainView> recentArticles = articleService.getArticles(0);
        CategoryForView categoryForView = categoryService.getCategoryForView();

        model.addAttribute("category",categoryForView);
        model.addAttribute("popularArticles", popularArticles);
        model.addAttribute("recentArticles",recentArticles);

        return "index";

    }



}
