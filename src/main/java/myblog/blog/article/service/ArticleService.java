package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.dto.ArticleDtoForMain;
import myblog.blog.article.dto.ArticleForm;
import myblog.blog.article.repository.ArticleRepository;
import myblog.blog.article.repository.NaArticleRepository;
import myblog.blog.category.domain.Category;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.member.doamin.Member;
import myblog.blog.member.repository.MemberRepository;
import myblog.blog.tags.service.TagsService;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    @Value("${git.gitToken}")
    private String gitToken;
    @Value("${git.repo}")
    private String gitRepo;

    private final TagsService tagsService;
    private final CategoryService categoryService;
    private final ArticleRepository articleRepository;
    private final NaArticleRepository naArticleRepository;
    private final ModelMapper modelMapper;

    /*
        - 아티클 작성 로직
    */
    public Article writeArticle(ArticleForm articleDto, Member writer) {

        Article newArticle = articleFrom(articleDto, writer);
        articleRepository.save(newArticle);
        tagsService.createNewTagsAndArticleTagList(articleDto.getTags(), newArticle);

        return newArticle;
    }

    /*
        - 아티클 수정 로직
    */
    public void editArticle(Long articleId, ArticleForm articleForm) {

        Article article = articleRepository.findById(articleId).get();
        Category category = categoryService.findCategory(articleForm.getCategory());
        tagsService.deleteArticleTags(article);
        tagsService.createNewTagsAndArticleTagList(articleForm.getTags(), article);
        articleForm.setThumbnailUrl(makeDefaultThumb(articleForm.getThumbnailUrl()));

        // 더티 체킹으로 업데이트
        article.editArticle(articleForm,category);
    }

    /*
        - 메인화면 위한 인기 아티클 6개 목록 가져오기
    */
    public List<ArticleDtoForMain> getPopularArticles() {
        List<Article> top6ByOrderByHitDesc = articleRepository.findTop6ByOrderByHitDesc();

        List<ArticleDtoForMain> articles = top6ByOrderByHitDesc.stream()
                .map(article -> modelMapper.map(article, ArticleDtoForMain.class))
                .collect(Collectors.toList());
 
        return articles;

    }

    public Slice<ArticleDtoForMain> getRecentArticles(int page) {

        Slice<ArticleDtoForMain> articles = articleRepository
                .findByOrderByIdDesc(PageRequest.of(page, 5))
                .map(article -> modelMapper
                        .map(article, ArticleDtoForMain.class));
        return articles;

    }

    public int getTotalArticleCntByCategory(String category, CategoryForView categorys) {

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
        throw new IllegalArgumentException("카테고리별 아티클 수 에러");
    }

    public Slice<ArticleDtoForMain> getArticlesByCategory(String category, Integer tier, Integer page) {

        Slice<Article> articles = null;

        if (tier.equals(0)) {
            articles = articleRepository
                    .findByOrderByIdDesc(
                            PageRequest.of(pageResolve(page), 5));
        } else if (tier.equals(1)) {
            articles = articleRepository
                    .findBySupCategoryOrderByIdDesc(
                            PageRequest.of(pageResolve(page), 5), category);

        } else {
            articles = articleRepository
                    .findBySubCategoryOrderByIdDesc(
                            PageRequest.of(pageResolve(page), 5), category);
        }

        return articles.map(article -> modelMapper
                .map(article, ArticleDtoForMain.class));
    }

    public Article readArticle(Long id){
         return articleRepository.findArticleByIdFetchCategoryAndTags(id);
    }

    public void addHit(Article article) {

        article.addHit();

    }

    public Article getArticleForEdit(Long articleId){

        return articleRepository.findArticleByIdFetchCategoryAndTags(articleId);

    }


    public void deleteArticle(Long articleId) {

          naArticleRepository.deleteArticle(articleId);
    }

    public List<Article> getArticlesByCategoryForDetailView(Category category){

        return articleRepository.findTop6ByCategoryOrderByIdDesc(category);

    }

    public Page<Article> getArticlesByTag(String tag, Integer page) {

        Page<Article> articles =
                articleRepository
                        .findAllByArticleTagsOrderById(PageRequest.of(pageResolve(page), 5), tag);

        return articles;

    }

    public Page<Article> getArticlesByKeyword(String keyword, Integer page) {

        Page<Article> articles =
                articleRepository
                        .findAllByKeywordOrderById(PageRequest.of(pageResolve(page),5), keyword);

        return articles;
    }

    public void pushArticleToGithub(Article article) {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(gitToken).build();
            GHRepository repository = gitHub.getRepository(gitRepo);
            repository.createContent()
                    .path(article.getCategory().getParents().getTitle()+"/"+article.getCategory().getTitle()+"/"+article.getTitle()+".md")
                    .content(article.getContent())
                    .message("test")
                    .branch("main")
                    .commit();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String makeDefaultThumb(String thumbnailUrl) {
        // 메시지로 올리기
        String defaultThumbUrl = "https://cdn.pixabay.com/photo/2020/11/08/13/28/tree-5723734_1280.jpg";

        if (thumbnailUrl == null || thumbnailUrl.equals("")) {
            thumbnailUrl = defaultThumbUrl;
        }
        return thumbnailUrl;
    }

    private int pageResolve(Integer rawPage) {
        if (rawPage == null || rawPage == 1) {
            return 0;
        } else return rawPage - 1;
    }

    private Article articleFrom(ArticleForm articleDto, Member writer) {

        return Article.builder()
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .toc(articleDto.getToc())
                .member(writer)
                .thumbnailUrl(makeDefaultThumb(articleDto.getThumbnailUrl()))
                .category(categoryService.findCategory(articleDto.getCategory()))
                .build();
    }

}
