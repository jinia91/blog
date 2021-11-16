package myblog.blog.article.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.domain.Article;
import myblog.blog.article.dto.ArticleDtoForDetail;
import myblog.blog.article.dto.ArticleDtoForMainView;
import myblog.blog.article.dto.NewArticleForm;
import myblog.blog.article.dto.PagingBoxDto;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.service.CategoryService;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.tags.dto.TagsDto;
import myblog.blog.tags.service.TagsService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ModelMapper modelMapper;
    private final ArticleService articleService;
    private final TagsService tagsService;
    private final CategoryService categoryService;

    @GetMapping("article/write")
    public String writeArticleForm(NewArticleForm newArticleForm, Model model) {

        List<CategoryNormalDto> categoryForInput =
                categoryService
                        .findCategoryByTier(2)
                        .stream()
                        .map(c -> modelMapper.map(c, CategoryNormalDto.class))
                        .collect(Collectors.toList());
        model.addAttribute("categoryInput", categoryForInput);

        List<TagsDto> tagsForInput =
                tagsService
                        .findAllTags()
                        .stream()
                        .map(c -> modelMapper.map(c, TagsDto.class))
                        .collect(Collectors.toList());
        model.addAttribute("tagsInput", tagsForInput);

        CategoryForView categoryForView = categoryService.getCategoryForView();
        model.addAttribute("category", categoryForView);

        model.addAttribute("articleDto", newArticleForm);

        return "article/articleWriteForm";
    }

    @PostMapping("article/write")
    @Transactional
    public String WriteArticle(@ModelAttribute NewArticleForm newArticleForm, Authentication authentication) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        newArticleForm.setMemberId(principal.getMemberId());
        articleService.writeArticle(newArticleForm);

        return "redirect:/";

    }


    @Transactional
    @GetMapping("article/list")
    public String getArticlesList(@RequestParam String category,
                                 @RequestParam Integer tier,
                                 @RequestParam Integer page,
                                 Model model) {

        CategoryForView categoryForView = categoryService.getCategoryForView();
        model.addAttribute("category", categoryForView);

        PagingBoxDto pagingBoxDto = PagingBoxDto.createOf(page, articleService.getTotalArticleCntByCategory(category, categoryForView));
        model.addAttribute("pagingBox", pagingBoxDto);

        Slice<ArticleDtoForMainView> articleList = articleService.getArticles(category,tier, pagingBoxDto.getCurPageNum());
        model.addAttribute("articleList", articleList);


        return "article/articleList";

    }


    @GetMapping("/article/view")
    public String readArticle(@RequestParam Long articleId,
                              Authentication authentication,
                              Model model){

        if(authentication != null) {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            model.addAttribute("picUrl", principal.getMemberPicUrl());
        }
        CategoryForView categoryForView = categoryService.getCategoryForView();
        model.addAttribute("category", categoryForView);

        Article article = articleService.findArticleById(articleId);
        model.addAttribute("article",modelMapper.map(article, ArticleDtoForDetail.class));

        return "article/articleView";
    }

    @GetMapping("/main/article/{pageNum}")
    public @ResponseBody
    List<ArticleDtoForMainView> mainNextPage(@PathVariable int pageNum) {

        return articleService.getArticles(pageNum).getContent();
    }


}
