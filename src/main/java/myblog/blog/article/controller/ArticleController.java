package myblog.blog.article.controller;

import lombok.RequiredArgsConstructor;

import myblog.blog.article.domain.Article;
import myblog.blog.article.service.*;
import myblog.blog.article.dto.*;
import myblog.blog.category.service.CategoryService;
import myblog.blog.category.dto.*;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.member.dto.MemberDto;
import myblog.blog.tags.service.TagsService;
import myblog.blog.tags.dto.TagsDto;
import myblog.blog.layout.LayoutDtoFactory;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;

import org.modelmapper.ModelMapper;

import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final TagsService tagsService;
    private final CategoryService categoryService;
    private final TempArticleService tempArticleService;

    private final LayoutDtoFactory layoutDtoFactory;

    private final ModelMapper modelMapper;
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;

    /*
        - 아티클 작성 폼 조회
    */
    @GetMapping("article/write")
    public String getWriteArticleForm(Model model) {
        layoutDtoFactory.AddLayoutTo(model);
        model.addAttribute("categoryInput", getCategoryDtosForForm());
        model.addAttribute("tagsInput", getTagsDtosForForm());
        model.addAttribute("articleDto", new ArticleForm());
        return "article/articleWriteForm";
    }
    /*
        - 아티클 작성 post 요청
    */
    @PostMapping("article/write")
    @Transactional
    public String writeArticle(@Validated ArticleForm articleForm,
                               @AuthenticationPrincipal PrincipalDetails principal,
                               Errors errors, Model model) {
        if (errors.hasErrors()) {
            getWriteArticleForm(model);
        }
        Long articleId = articleService.writeArticle(articleForm, principal.getMember());
        articleService.pushArticleToGithub(articleId);
        tempArticleService.deleteTemp();

        return "redirect:/article/view?articleId=" + articleId;
    }

    /*
        - 아티클 수정 폼 조회
    */
    @GetMapping("/article/edit")
    public String updateArticle(@RequestParam Long articleId,
                                Model model) {
        // 기존 아티클 DTO 전처리
        Article article = articleService.readArticle(articleId);
        ArticleDtoForEdit articleDto = modelMapper.map(article, ArticleDtoForEdit.class);
        List<String> articleTagStrings = article.getArticleTagLists()
                .stream()
                .map(articleTag -> articleTag.getTags().getName())
                .collect(Collectors.toList());
        articleDto.setArticleTagList(articleTagStrings);
        //
        layoutDtoFactory.AddLayoutTo(model);
        model.addAttribute("categoryInput", getCategoryDtosForForm());
        model.addAttribute("tagsInput", getTagsDtosForForm());;
        model.addAttribute("articleDto", articleDto);
        return "article/articleEditForm";
    }

    /*
        - 아티클 수정 요청
    */
    @PostMapping("/article/edit")
    @Transactional
    public String editArticle(@RequestParam Long articleId,
                              @ModelAttribute ArticleForm articleForm) {
        articleService.editArticle(articleId, articleForm);
        return "redirect:/article/view?articleId=" + articleId;
    }

    /*
        - 아티클 삭제 요청
    */
    @PostMapping("/article/delete")
    @Transactional
    public String deleteArticle(@RequestParam Long articleId) {
        articleService.deleteArticle(articleId);
        return "redirect:/";
    }

    /*
        - 카테고리별 게시물 조회하기
    */
    @Transactional
    @GetMapping("article/list")
    public String getArticlesListByCategory(@RequestParam String category,
                                            @RequestParam Integer tier,
                                            @RequestParam Integer page,
                                            Model model) {
        // DTO 매핑 전처리
        PagingBoxDto pagingBoxDto =
                PagingBoxDto.createOf(page, getTotalArticleCntByCategory(category, categoryService.getCategoryForView()));

        Slice<ArticleDtoForCardBox> articleDtoList =
                articleService.getArticlesByCategory(category, tier, pagingBoxDto.getCurPageNum())
                        .map(article -> modelMapper.map(article, ArticleDtoForCardBox.class));
        //

        for(ArticleDtoForCardBox articleDto : articleDtoList){
            articleDto.setContent(Jsoup.parse(htmlRenderer.render(parser.parse(articleDto.getContent()))).text());
        }

        layoutDtoFactory.AddLayoutTo(model);
        model.addAttribute("pagingBox", pagingBoxDto);
        model.addAttribute("articleList", articleDtoList);

        return "article/articleList";
    }

    /*
        - 태그별 게시물 조회하기
    */
    @Transactional
    @GetMapping("article/list/tag/")
    public String getArticlesListByTag(@RequestParam Integer page,
                                  @RequestParam String tagName,
                                  Model model) {
        // DTO 매핑 전처리
        Page<ArticleDtoForCardBox> articleList =
                articleService.getArticlesByTag(tagName, page)
                        .map(article ->
                                modelMapper.map(article, ArticleDtoForCardBox.class));

        for(ArticleDtoForCardBox article : articleList){
            article.setContent(Jsoup.parse(htmlRenderer.render(parser.parse(article.getContent()))).text());
        }

        PagingBoxDto pagingBoxDto =
                PagingBoxDto.createOf(page, (int)articleList.getTotalElements());

        layoutDtoFactory.AddLayoutTo(model);
        model.addAttribute("articleList", articleList);
        model.addAttribute("pagingBox", pagingBoxDto);

        return "article/articleListByTag";
    }

    /*
        - 검색어별 게시물 조회하기
    */
    @Transactional
    @GetMapping("article/list/search/")
    public String getArticlesListByKeyword(@RequestParam Integer page,
                                  @RequestParam String keyword,
                                  Model model) {
        // DTO 매핑 전처리
        Page<ArticleDtoForCardBox> articleList =
                articleService.getArticlesByKeyword(keyword, page)
                        .map(article ->
                                modelMapper.map(article, ArticleDtoForCardBox.class));

        for(ArticleDtoForCardBox article : articleList){
            article.setContent(Jsoup.parse(htmlRenderer.render(parser.parse(article.getContent()))).text());
        }

        PagingBoxDto pagingBoxDto =
                PagingBoxDto.createOf(page, (int)articleList.getTotalElements());

        layoutDtoFactory.AddLayoutTo(model);
        model.addAttribute("articleList", articleList);
        model.addAttribute("pagingBox", pagingBoxDto);

        return "article/articleListByKeyword";

    }

    /*
        - 아티클 상세 조회
            1. 로그인여부 검토
            2. 게시물 상세조회에 필요한 Dto 전처리
            3. 메타태그 작성위한 Dto 전처리
            4. Dto 담기
            5. 조회수 증가 검토
    */
    @Transactional
    @GetMapping("/article/view")
    public String readArticle(@RequestParam Long articleId,
                              @AuthenticationPrincipal PrincipalDetails principal,
                              @CookieValue(required = false, name = "view") String cookie,
                              HttpServletResponse response,
                              Model model) {
        // 1. 로그인 여부에 따라 뷰단에 회원정보 출력 여부 결정
        if (principal != null) {
            model.addAttribute("member", modelMapper.map(principal.getMember(), MemberDto.class));
        } else {
            model.addAttribute("member", null);
        }

        /*
            DTO 매핑 전처리
            2. 게시물 상세조회용
        */
        Article article = articleService.readArticle(articleId);

        ArticleDtoForDetail articleDtoForDetail =
                modelMapper.map(article, ArticleDtoForDetail.class);

        List<String> tags =
                article.getArticleTagLists()
                    .stream()
                    .map(tag -> tag.getTags().getName())
                    .collect(Collectors.toList());

        articleDtoForDetail.setTags(tags);
        articleDtoForDetail.setContent(htmlRenderer.render(parser.parse(article.getContent())));

        List<ArticleDtoByCategory> articleTitlesSortByCategory =
                articleService
                        .getArticlesByCategoryForDetailView(article.getCategory())
                        .stream()
                        .map(article1 -> modelMapper.map(article1, ArticleDtoByCategory.class))
                        .collect(Collectors.toList());

        // 3. 메타 태그용 Dto 전처리
        StringBuilder metaTags = new StringBuilder();
        for (String tag : tags) {
            metaTags.append(tag).append(", ");
        }

        String substringContents = null;
        if(articleDtoForDetail.getContent().length()>200) {
            substringContents = articleDtoForDetail.getContent().substring(0, 200);
        }
        else substringContents = articleDtoForDetail.getContent();

        // 4. 모델 담기
        layoutDtoFactory.AddLayoutTo(model);
        model.addAttribute("article", articleDtoForDetail);
        model.addAttribute("metaTags",metaTags);
        model.addAttribute("metaContents",Jsoup.parse(substringContents).text());
        model.addAttribute("articlesSortBycategory", articleTitlesSortByCategory);

        // 5. 조회수 증가 검토
        addHitWithCookie(article, cookie, response);

        return "article/articleView";
    }


    /*
        - 쿠키 추가 / 조회수 증가 검토
    */
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
            }
            if (!isRead) {
                cookie += articleId + "/";
                article.addHit();
            }
            response.addCookie(new Cookie("view", cookie));
        }
    }

    /*
        - 카테고리별 아티클 갯수 구하기
    */
    private int getTotalArticleCntByCategory(String category, CategoryForView categorys) {

        if (categorys.getTitle().equals(category)) {
            return categorys.getCount();
        } else {
            for (CategoryForView categoryCnt :
                    categorys.getCategoryTCountList()) {
                if (categoryCnt.getTitle().equals(category))
                    return categoryCnt.getCount();
                for (CategoryForView categoryCntSub : categoryCnt.getCategoryTCountList()) {
                    if (categoryCntSub.getTitle().equals(category))
                        return categoryCntSub.getCount();
                }
            }
        }
        throw new IllegalArgumentException("'"+category+"' 라는 카테고리는 존재하지 않습니다.");
    }

    /*
- 아티클 폼에 필요한 태그 dtos
    */
    private List<TagsDto> getTagsDtosForForm() {
        return tagsService
                .findAllTags()
                .stream()
                .map(tag -> new TagsDto(tag.getName()))
                .collect(Collectors.toList());
    }

    /*
    - 아티클 폼에 필요한 카테고리 dtos
    */
    private List<CategoryNormalDto> getCategoryDtosForForm() {
        return categoryService
                .findCategoryByTier(2)
                .stream()
                .map(category -> modelMapper.map(category, CategoryNormalDto.class))
                .collect(Collectors.toList());
    }
}
