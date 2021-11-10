package myblog.blog.article.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.dto.ArticleForMainView;
import myblog.blog.article.dto.NewArticleDto;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.dto.CategoryForMainView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.tags.service.TagsService;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final TagsService tagsService;
    private final CategoryService categoryService;

    @GetMapping("article/write")
    public String writeArticleForm(NewArticleDto newArticleDto, Model model){


        CategoryForMainView categoryForView = categoryService.getCategoryForView();

        model.addAttribute("category",categoryForView);
        model.addAttribute(newArticleDto);

        return "article/articleWriteForm";
    }

    @PostMapping("article/write")
    @Transactional
    public String WriteArticle(@ModelAttribute NewArticleDto newArticleDto, Authentication authentication){


        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        newArticleDto.setMemberId(principal.getMemberId());

        articleService.writeArticle(newArticleDto);

        return "redirect:/";

    }

    @GetMapping("/main/article/{pageNum}")
    public @ResponseBody
    List<ArticleForMainView> nextPage(@PathVariable int pageNum){

        return articleService.getRecentArticles(pageNum).getContent();
    }


}
