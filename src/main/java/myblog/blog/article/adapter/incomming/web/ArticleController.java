package myblog.blog.article.adapter.incomming.web;

import lombok.RequiredArgsConstructor;

import myblog.blog.article.application.port.request.ArticleCreateRequest;
import myblog.blog.article.application.port.request.ArticleEditRequest;
import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.article.application.port.incomming.TempArticleUseCase;
import myblog.blog.article.application.port.response.ArticleResponseForCardBox;
import myblog.blog.article.model.*;
import myblog.blog.article.application.port.incomming.ArticleQueriesUseCase;
import myblog.blog.article.application.port.incomming.TagsQueriesUseCase;
import myblog.blog.category.service.CategoryService;
import myblog.blog.category.dto.*;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.member.dto.MemberDto;
import myblog.blog.shared.queries.LayoutRenderingQueries;

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

import static myblog.blog.shared.utils.MarkdownUtils.*;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleUseCase articleUseCase;
    private final ArticleQueriesUseCase articleQueriesUseCase;
    private final TempArticleUseCase tempArticleUseCase;
    private final CategoryService categoryService;
    private final TagsQueriesUseCase tagsQueriesUseCase;
    private final LayoutRenderingQueries layoutRenderingQueries;
    private final ModelMapper modelMapper;

    @GetMapping("article/write")
    String getArticleWriteForm(Model model) {
        layoutRenderingQueries.AddLayoutTo(model);
        model.addAttribute("categoryInput", getCategoryDtosForForm());
        model.addAttribute("tagsInput", tagsQueriesUseCase.findAllTagDtos());
        model.addAttribute("articleDto", new ArticleForm());
        return "article/articleWriteForm";
    }
    /*
        - 아티클 작성 post 요청
    */
    @PostMapping("article/write")
    @Transactional
    String writeArticle(@Validated ArticleForm articleForm,
                               @AuthenticationPrincipal PrincipalDetails principal,
                               Errors errors, Model model) {
        if (errors.hasErrors()) {
            getArticleWriteForm(model);
        }
        Long articleId = articleUseCase.writeArticle(ArticleCreateRequest.from(articleForm,principal.getMemberId()));
        articleUseCase.backupArticle(articleId);
        tempArticleUseCase.deleteTemp();
        return "redirect:/article/view?articleId=" + articleId;
    }
    /*
        - 아티클 수정 폼 조회
    */
    @GetMapping("/article/edit")
    String updateArticle(@RequestParam Long articleId, Model model) {
        ArticleResponseForEdit articleDto = articleQueriesUseCase.getArticleForEdit(articleId);
        layoutRenderingQueries.AddLayoutTo(model);
        model.addAttribute("categoryInput", getCategoryDtosForForm());
        model.addAttribute("tagsInput", tagsQueriesUseCase.findAllTagDtos());
        model.addAttribute("articleDto", articleDto);
        return "article/articleEditForm";
    }

    /*
        - 아티클 수정 요청
    */
    @PostMapping("/article/edit")
    @Transactional
    String editArticle(@RequestParam Long articleId,
                       @ModelAttribute ArticleForm articleForm) {
        articleUseCase.editArticle(ArticleEditRequest.from(articleId, articleForm));
        return "redirect:/article/view?articleId=" + articleId;
    }

    /*
        - 아티클 삭제 요청
    */
    @PostMapping("/article/delete")
    @Transactional
    String deleteArticle(@RequestParam Long articleId) {
        articleUseCase.deleteArticle(articleId);
        return "redirect:/";
    }

    /*
        - 카테고리별 게시물 조회하기
    */
    @Transactional
    @GetMapping("article/list")
    String getArticlesListByCategory(@RequestParam String category,
                                     @RequestParam Integer tier,
                                     @RequestParam Integer page,
                                     Model model) {
        PagingBoxDto pagingBoxDto =
                PagingBoxDto.createOf(page, getTotalArticleCntByCategory(category, categoryService.getCategoryForView()));

        Slice<ArticleResponseForCardBox> articleDtoList =
                articleQueriesUseCase.getArticlesByCategory(category, tier, pagingBoxDto.getCurPageNum());

        for(ArticleResponseForCardBox articleDto : articleDtoList){
            articleDto.setContent(Jsoup.parse(getHtmlRenderer().render(getParser().parse(articleDto.getContent()))).text());
        }

        layoutRenderingQueries.AddLayoutTo(model);
        model.addAttribute("pagingBox", pagingBoxDto);
        model.addAttribute("articleList", articleDtoList);

        return "article/articleList";
    }

    /*
        - 태그별 게시물 조회하기
    */
    @Transactional
    @GetMapping("article/list/tag/")
    String getArticlesListByTag(@RequestParam Integer page,
                                @RequestParam String tagName,
                                Model model) {
        Page<ArticleResponseForCardBox> articleList =
                articleQueriesUseCase.getArticlesByTag(tagName, page);

        for(ArticleResponseForCardBox article : articleList){
            article.setContent(Jsoup.parse(getHtmlRenderer().render(getParser().parse(article.getContent()))).text());
        }

        PagingBoxDto pagingBoxDto =
                PagingBoxDto.createOf(page, (int)articleList.getTotalElements());

        layoutRenderingQueries.AddLayoutTo(model);
        model.addAttribute("articleList", articleList);
        model.addAttribute("pagingBox", pagingBoxDto);

        return "article/articleListByTag";
    }

    /*
        - 검색어별 게시물 조회하기
    */
    @Transactional
    @GetMapping("article/list/search/")
    String getArticlesListByKeyword(@RequestParam Integer page,
                                    @RequestParam String keyword,
                                    Model model) {
        Page<ArticleResponseForCardBox> articleList =
                articleQueriesUseCase.getArticlesByKeyword(keyword, page);

        for(ArticleResponseForCardBox article : articleList){
            article.setContent(Jsoup.parse(getHtmlRenderer().render(getParser().parse(article.getContent()))).text());
        }

        PagingBoxDto pagingBoxDto =
                PagingBoxDto.createOf(page, (int)articleList.getTotalElements());

        layoutRenderingQueries.AddLayoutTo(model);
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
    String readArticle(@RequestParam Long articleId,
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
            2.화면단을 위한 처리
        */
        ArticleDtoForDetail articleDtoForDetail = articleQueriesUseCase.getArticleForDetail(articleId);
        articleDtoForDetail.setContent(getHtmlRenderer().render(getParser().parse(articleDtoForDetail.getContent())));

        List<ArticleResponseByCategory> articleTitlesSortByCategory =
                articleQueriesUseCase
                        .getArticlesByCategoryForDetailView(articleDtoForDetail.getCategory());

        // 3. 메타 태그용 Dto 전처리
        StringBuilder metaTags = new StringBuilder();
        for (String tag : articleDtoForDetail.getTags()) {
            metaTags.append(tag).append(", ");
        }

        String substringContents = null;
        if(articleDtoForDetail.getContent().length()>200) {
            substringContents = articleDtoForDetail.getContent().substring(0, 200);
        }
        else substringContents = articleDtoForDetail.getContent();

        // 4. 모델 담기
        layoutRenderingQueries.AddLayoutTo(model);
        model.addAttribute("article", articleDtoForDetail);
        model.addAttribute("metaTags",metaTags);
        model.addAttribute("metaContents",Jsoup.parse(substringContents).text());
        model.addAttribute("articlesSortBycategory", articleTitlesSortByCategory);

        // 5. 조회수 증가 검토 및 증가
        if(needToAddHitThroughCheckingCookie(articleId, cookie, response)) articleUseCase.addHit(articleId);

        return "article/articleView";
    }

    /*
        - 쿠키 추가 / 조회수 증가 검토
    */
    private boolean needToAddHitThroughCheckingCookie(Long articleId, String cookie, HttpServletResponse response) {
        if (cookie == null) {
            Cookie viewCookie = new Cookie("view", articleId + "/");
            viewCookie.setComment("게시물 조회 확인용");
            viewCookie.setMaxAge(60 * 60);
            response.addCookie(viewCookie);
            return true;
        } else {
            boolean addHitAvailable = false;
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
                addHitAvailable = true;
            }
            response.addCookie(new Cookie("view", cookie));
            return addHitAvailable;
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
    - 아티클 폼에 필요한 카테고리 dtos
    */
    private List<CategorySimpleView> getCategoryDtosForForm() {
        return categoryService
                .findCategoryByTier(2)
                .stream()
                .map(category -> modelMapper.map(category, CategorySimpleView.class))
                .collect(Collectors.toList());
    }
}
