package myblog.blog.article.adapter.incomming;

import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.article.application.port.incomming.TempArticleUseCase;
import myblog.blog.article.application.port.incomming.ArticleQueriesUseCase;
import myblog.blog.article.application.port.incomming.TagsQueriesUseCase;
import myblog.blog.category.appliacation.port.incomming.CategoryQueriesUseCase;
import myblog.blog.category.domain.CategoryNotFoundException;
import myblog.blog.shared.application.port.incomming.LayoutRenderingUseCase;

import myblog.blog.article.application.port.incomming.request.ArticleCreateCommand;
import myblog.blog.article.application.port.incomming.request.ArticleEditCommand;
import myblog.blog.category.appliacation.port.incomming.response.CategoryViewForLayout;
import myblog.blog.member.application.port.incomming.response.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import myblog.blog.shared.utils.MetaTagBuildUtils;
import org.jsoup.Jsoup;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleUseCase articleUseCase;
    private final ArticleQueriesUseCase articleQueriesUseCase;
    private final TempArticleUseCase tempArticleUseCase;
    private final TagsQueriesUseCase tagsQueriesUseCase;
    private final CategoryQueriesUseCase categoryQueriesUseCase;
    private final LayoutRenderingUseCase layoutRenderingUseCase;

    @GetMapping("article/write")
    String getArticleWriteForm(Model model) {
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("categoryInput", categoryQueriesUseCase.findCategoryByTier(2));
        model.addAttribute("tagsInput", tagsQueriesUseCase.findAllTagDtos());
        model.addAttribute("articleDto", new ArticleForm());
        return "article/articleWriteForm";
    }

    @PostMapping("article/write")
    @Transactional
    String writeArticle(@Validated ArticleForm articleForm,
                        @AuthenticationPrincipal PrincipalDetails principal,
                        Errors errors, Model model) {
        if (errors.hasErrors()) getArticleWriteForm(model);
        var command = ArticleCreateCommand.from(articleForm, principal.getMemberId());
        var articleId = articleUseCase.writeArticle(command);
        articleUseCase.backupArticle(articleId);
        tempArticleUseCase.deleteTemp();
        return "redirect:/article/view?articleId=" + articleId;
    }
    /*
        - 아티클 수정 폼 조회
    */
    @GetMapping("/article/edit")
    String updateArticle(@RequestParam Long articleId, Model model) {
        var articleDto = articleQueriesUseCase.getArticleForEdit(articleId);
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("categoryInput", categoryQueriesUseCase.findCategoryByTier(2));
        model.addAttribute("tagsInput", tagsQueriesUseCase.findAllTagDtos());
        model.addAttribute("articleDto", articleDto);
        return "article/articleEditForm";
    }
    @PostMapping("/article/edit")
    @Transactional
    String editArticle(@RequestParam Long articleId,
                       @ModelAttribute ArticleForm articleForm) {
        var command = ArticleEditCommand.from(articleId, articleForm);
        articleUseCase.editArticle(command);
        return "redirect:/article/view?articleId=" + articleId;
    }
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
                                     @RequestParam int tier,
                                     @RequestParam int page,
                                     Model model) {
        int totalArticleCnt = getTotalArticleCntByCategory(category, categoryQueriesUseCase.getCategoryViewForLayout());
        var pagingBoxHandler =
                PagingBoxHandler.createOf(page, totalArticleCnt);
        var articleDtoList = articleQueriesUseCase.getArticlesByCategory(category, tier, pagingBoxHandler.getCurPageNum());
        for(var articleDto : articleDtoList) articleDto.parseAndRenderForView();
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("pagingBox", pagingBoxHandler);
        model.addAttribute("articleList", articleDtoList);
        return "article/articleList";
    }
    private int getTotalArticleCntByCategory(String category, CategoryViewForLayout categorys) {
        if (categorys.getTitle().equals(category)) return categorys.getCount();
        for (var categoryCnt : categorys.getCategoryTCountList()) {
            if (categoryCnt.getTitle().equals(category)) return categoryCnt.getCount();
            for (var categoryCntSub : categoryCnt.getCategoryTCountList()) {
                if (categoryCntSub.getTitle().equals(category)) return categoryCntSub.getCount();
            }
        }
        throw new CategoryNotFoundException();
    }
    /*
        - 태그별 게시물 조회하기
    */
    @GetMapping("article/list/tag/")
    String getArticlesListByTag(@RequestParam Integer page,
                                @RequestParam String tagName,
                                Model model) {
        var articleList = articleQueriesUseCase.getArticlesByTag(tagName, page);
        for(var article : articleList) article.parseAndRenderForView();
        var pagingBoxHandler = PagingBoxHandler.createOf(page, (int)articleList.getTotalElements());
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("articleList", articleList);
        model.addAttribute("pagingBox", pagingBoxHandler);
        return "article/articleListByTag";
    }
    /*
        - 검색어별 게시물 조회하기
    */
    @GetMapping("article/list/search/")
    String getArticlesListByKeyword(@RequestParam Integer page,
                                    @RequestParam String keyword,
                                    Model model) {
        var articleList = articleQueriesUseCase.getArticlesByKeyword(keyword, page);
        for(var article : articleList) article.parseAndRenderForView();
        var pagingBoxHandler = PagingBoxHandler.createOf(page, (int)articleList.getTotalElements());
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("articleList", articleList);
        model.addAttribute("pagingBox", pagingBoxHandler);
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
                       HttpServletResponse response, Model model) {
        addMemberInfoToModel(principal, model);
        var articleResponseForDetail = articleQueriesUseCase.getArticleForDetail(articleId);
        articleResponseForDetail.parseAndRenderForView();
        var articleTitlesSortByCategory = articleQueriesUseCase
                        .getArticlesByCategoryForDetailView(articleResponseForDetail.getCategory());
        var metaTags = MetaTagBuildUtils.buildMetaTags(articleResponseForDetail.getTags());
        var substringContents = getSubStringContentsFrom(articleResponseForDetail.getContent());
        layoutRenderingUseCase.AddLayoutTo(model);
        model.addAttribute("article", articleResponseForDetail);
        model.addAttribute("metaTags",metaTags);
        model.addAttribute("metaContents",Jsoup.parse(substringContents).text());
        model.addAttribute("articlesSortBycategory", articleTitlesSortByCategory);
        if(needToAddHitThroughCheckingCookie(articleId, cookie, response)) articleUseCase.addHit(articleId);
        return "article/articleView";
    }

    private void addMemberInfoToModel(PrincipalDetails principal, Model model) {
        if (principal != null) {
            model.addAttribute("member", principal.getMember());
        } else {
            model.addAttribute("member", null);
        }
    }

    private String getSubStringContentsFrom(String content) {
        String substringContents = null;
        if(content.length()>200) {
            substringContents = content.substring(0, 200);
        }
        else substringContents = content;
        return substringContents;
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
}
