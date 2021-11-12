package myblog.blog.article.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.dto.ArticleForMainView;
import myblog.blog.article.dto.NewArticleDto;
import myblog.blog.article.service.ArticleService;
import myblog.blog.category.dto.CategoryForMainView;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.service.CategoryService;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.tags.dto.TagsDto;
import myblog.blog.tags.service.TagsService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public String writeArticleForm(NewArticleDto newArticleDto, Model model) {

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

        CategoryForMainView categoryForView = categoryService.getCategoryForView();
        model.addAttribute("category", categoryForView);

        model.addAttribute("articleDto", newArticleDto);

        return "article/articleWriteForm";
    }

    @PostMapping("article/write")
    @Transactional
    public String WriteArticle(@ModelAttribute NewArticleDto newArticleDto, Authentication authentication) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        newArticleDto.setMemberId(principal.getMemberId());
        articleService.writeArticle(newArticleDto);

        return "redirect:/";

    }


    @GetMapping("article/list")
    public String getArticlesList(@RequestParam String category,
                                 @RequestParam Integer tier,
                                 @RequestParam(required = false) Integer page,
                                 Model model) {

        CategoryForMainView categoryForView = categoryService.getCategoryForView();
        Page<ArticleForMainView> articleList = articleService.getArticles(category,tier, page);

        model.addAttribute("category", categoryForView);
        model.addAttribute("articleList", articleList);

        return "article/articleList";

    }




    @GetMapping("/main/article/{pageNum}")
    public @ResponseBody
    List<ArticleForMainView> mainNextPage(@PathVariable int pageNum) {

        return articleService.getArticles(pageNum).getContent();
    }


}
