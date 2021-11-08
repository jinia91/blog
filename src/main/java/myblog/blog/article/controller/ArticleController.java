package myblog.blog.article.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.dto.NewArticleDto;
import myblog.blog.article.service.ArticleService;
import myblog.blog.member.auth.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("article/write")
    public String writeArticleForm(NewArticleDto newArticleDto, Model model){

        model.addAttribute(newArticleDto);

        return "articleWriteForm";
    }

    @PostMapping("article/write")
    public String WriteArticle(@ModelAttribute NewArticleDto newArticleDto, Authentication authentication){

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        newArticleDto.setMemberId(principal.getMemberId());

        Long articleId = articleService.writeArticle(newArticleDto);

        return "redirect:/";

    }

}
