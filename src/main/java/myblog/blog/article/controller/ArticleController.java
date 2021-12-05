package myblog.blog.article.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myblog.blog.article.domain.Article;
import myblog.blog.article.dto.*;
import myblog.blog.article.service.ArticleService;
import myblog.blog.article.service.TempArticleService;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.service.CategoryService;
import myblog.blog.comment.dto.CommentDtoForSide;
import myblog.blog.comment.service.CommentService;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.member.dto.MemberDto;
import myblog.blog.tags.dto.TagsDto;
import myblog.blog.tags.service.TagsService;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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
    private final CommentService commentService;
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;
    private final TempArticleService tempArticleService;

    @GetMapping("article/write")
    public String writeArticleForm(ArticleForm articleForm, Model model) {

        List<CategoryNormalDto> categoryForInput =
                categoryService
                        .findCategoryByTier(2)
                        .stream()
                        .map(category -> modelMapper.map(category, CategoryNormalDto.class))
                        .collect(Collectors.toList());
        model.addAttribute("categoryInput", categoryForInput);

        List<TagsDto> tagsForInput =
                tagsService
                        .findAllTags()
                        .stream()
                        .map(tag -> new TagsDto(tag.getName()))
                        .collect(Collectors.toList());
        model.addAttribute("tagsInput", tagsForInput);

        CategoryForView categoryForView = CategoryForView.createCategory(categoryService.getCategoryForView());
        model.addAttribute("category", categoryForView);

        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent(), comment.isSecret() ))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);

        model.addAttribute("articleDto", articleForm);

        return "article/articleWriteForm";
    }


    /*
        - 넘어온 articleForm을 저장
    */
    @PostMapping("article/write")
    @Transactional
    public String writeArticle(ArticleForm articleForm, @AuthenticationPrincipal PrincipalDetails principal) {

        Article article = articleService.writeArticle(articleForm, principal.getMember());

//        articleService.pushArticleToGithub(article);
        tempArticleService.deleteTemp();

        return "redirect:/article/view?articleId=" + article.getId();

    }

    @Transactional
    @GetMapping("article/list")
    public String getArticlesList(@RequestParam String category,
                                  @RequestParam Integer tier,
                                  @RequestParam Integer page,
                                  Model model) {

        CategoryForView categoryForView = CategoryForView.createCategory(categoryService.getCategoryForView());
        model.addAttribute("category", categoryForView);
        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent(), comment.isSecret()))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);

        PagingBoxDto pagingBoxDto = PagingBoxDto.createOf(page, articleService.getTotalArticleCntByCategory(category, categoryForView));
        model.addAttribute("pagingBox", pagingBoxDto);

        Slice<ArticleDtoForMain> articleList = articleService.getArticlesByCategory(category, tier, pagingBoxDto.getCurPageNum());
        model.addAttribute("articleList", articleList);


        return "article/articleList";

    }

    @Transactional
    @GetMapping("article/list/tag/")
    public String getArticlesListByTag(@RequestParam Integer page,
                                  @RequestParam String tagName,
                                  Model model) {

        CategoryForView categoryForView = CategoryForView.createCategory(categoryService.getCategoryForView());
        model.addAttribute("category", categoryForView);
        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent(), comment.isSecret()))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);


        Page<ArticleDtoForMain> articleList =
                articleService.getArticlesByTag(tagName, page)
                        .map(article ->
                                modelMapper.map(article, ArticleDtoForMain.class));
        model.addAttribute("articleList", articleList);

        PagingBoxDto pagingBoxDto = PagingBoxDto.createOf(page, articleList.getTotalPages());
        model.addAttribute("pagingBox", pagingBoxDto);

        return "article/articleListByTag";

    }

    @Transactional
    @GetMapping("article/list/search/")
    public String getArticlesListByKeyword(@RequestParam Integer page,
                                  @RequestParam String keyword,
                                  Model model) {

        CategoryForView categoryForView = CategoryForView.createCategory(categoryService.getCategoryForView());
        model.addAttribute("category", categoryForView);
        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent(),comment.isSecret()))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);

        Page<ArticleDtoForMain> articleList =
                articleService.getArticlesByKeyword(keyword, page)
                        .map(article ->
                                modelMapper.map(article, ArticleDtoForMain.class));
        model.addAttribute("articleList", articleList);

        PagingBoxDto pagingBoxDto = PagingBoxDto.createOf(page, articleList.getTotalPages());
        model.addAttribute("pagingBox", pagingBoxDto);

        return "article/articleListByKeyword";

    }


    @GetMapping("/article/view")
    public String readArticle(@RequestParam Long articleId,
                              Authentication authentication,
                              @CookieValue(required = false, name = "view") String cookie, HttpServletResponse response,
                              Model model) {

        if (authentication != null) {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            MemberDto memberDto = modelMapper.map(principal.getMember(), MemberDto.class);
            model.addAttribute("member", memberDto);
        } else {
            model.addAttribute("member", null);
        }

        CategoryForView categoryForView = CategoryForView.createCategory(categoryService.getCategoryForView());
        model.addAttribute("category", categoryForView);
        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent(),comment.isSecret()))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);

        Article article = articleService.readArticle(articleId);
        ArticleDtoForDetail articleDtoForDetail = modelMapper.map(article, ArticleDtoForDetail.class);

        List<String> tags = article.getArticleTagLists()
                .stream()
                .map(tag -> tag.getTags().getName())
                .collect(Collectors.toList());

        articleDtoForDetail
                .setTags(tags);

        articleDtoForDetail
                .setContent(
                        htmlRenderer.render(parser.parse(article.getContent()))
                );

        model.addAttribute("article", articleDtoForDetail);

//        메타태그 삽입
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append(tag).append(", ");
        }
        model.addAttribute("metaTags",sb);

        String substringContents = null;
        if(articleDtoForDetail.getContent().length()>200) {
            substringContents = articleDtoForDetail.getContent().substring(0, 200);
        }
        else substringContents = articleDtoForDetail.getContent();

        model.addAttribute("metaContents",Jsoup.parse(substringContents).text());
//

        List<ArticleDtoByCategory> articleTitlesSortByCategory =
                articleService
                        .getArticlesByCategoryForDetailView(article.getCategory())
                        .stream()
                        .map(article1 -> modelMapper.map(article1, ArticleDtoByCategory.class))
                        .collect(Collectors.toList());
        model.addAttribute("articlesSortBycategory", articleTitlesSortByCategory);

        addHitWithCookie(article, cookie, response);

        return "article/articleView";
    }

    @GetMapping("/article/edit")
    public String updateArticle(@RequestParam Long articleId,
                                Authentication authentication,
                                Model model) {

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


        Article article = articleService.getArticleForEdit(articleId);
        ArticleDtoForEdit articleDto = modelMapper.map(article, ArticleDtoForEdit.class);

        List<String> tagList = article.getArticleTagLists()
                .stream()
                .map(articleTag -> articleTag.getTags().getName())
                .collect(Collectors.toList());

        articleDto.setArticleTagList(tagList);

        model.addAttribute("articleDto", articleDto);

        CategoryForView categoryForView = CategoryForView.createCategory(categoryService.getCategoryForView());
        model.addAttribute("category", categoryForView);
        List<CommentDtoForSide> comments = commentService.recentCommentList()
                .stream()
                .map(comment ->
                        new CommentDtoForSide(comment.getId(), comment.getArticle().getId(), comment.getContent(),comment.isSecret()))
                .collect(Collectors.toList());
        model.addAttribute("commentsList", comments);

        return "article/articleEditForm";
    }

    @PostMapping("/article/delete")
    @Transactional
    public String deleteArticle(@RequestParam Long articleId,
                                Authentication authentication) {

        articleService.deleteArticle(articleId);

        return "redirect:/";

    }

    @PostMapping("/article/edit")
    @Transactional
    public String editArticle(@RequestParam Long articleId,
                              @ModelAttribute ArticleForm articleForm, @AuthenticationPrincipal PrincipalDetails principal) {

        articleService.editArticle(articleId, articleForm);


        return "redirect:/article/view?articleId=" + articleId;

    }

    @GetMapping("/main/article/{pageNum}")
    public @ResponseBody
    List<ArticleDtoForMain> mainNextPage(@PathVariable int pageNum) {

        return articleService.getRecentArticles(pageNum).getContent();
    }

    private void addHitWithCookie(Article article, String cookie, HttpServletResponse response) {
        Long articleId = article.getId();
        if (cookie == null) {
            Cookie viewCookie = new Cookie("view", articleId + "/");
            viewCookie.setComment("게시물 조회 확인용");
            viewCookie.setMaxAge(60 * 60);
            articleService.addHit(article);
            response.addCookie(viewCookie);
        } else {
            boolean isRead = false;
            String[] viewCookieList = cookie.split("/");
            for (String alreadyRead : viewCookieList) {
                if (alreadyRead.equals(String.valueOf(articleId))) {
                    isRead = true;
                    break;
                }
                ;
            }
            if (!isRead) {
                cookie += articleId + "/";
                articleService.addHit(article);
            }
            response.addCookie(new Cookie("view", cookie));
        }
    }


}
