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

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    @Value("${git.gitToken}")
    private String gitToken;
    @Value("${git.repo}")
    private String gitRepo;

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final TagsService tagsService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final NaArticleRepository naArticleRepository;

    public Article writeArticle(ArticleForm articleDto) {

        Article newArticle = articleFrom(articleDto);

        articleRepository.save(newArticle);
        tagsService.createNewTagsAndArticleTagList(articleDto.getTags(), newArticle);

        return newArticle;

    }

    public List<ArticleDtoForMain> getPopularArticles() {
        List<Article> top6ByOrderByHitDesc = articleRepository.findTop6ByOrderByHitDesc();

        List<ArticleDtoForMain> articles = new ArrayList<>();

        for (Article article : top6ByOrderByHitDesc) {
            articles.add(modelMapper.map(article, ArticleDtoForMain.class));
        }


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

        if (tier.intValue() == 0) {
            articles = articleRepository
                    .findAllByOrderByIdDesc(
                            PageRequest.of(pageResolver(page), 5));
        } else if (tier.intValue() == 1) {
            articles = articleRepository
                    .findByT1CategoryOrderByIdDesc(
                            PageRequest.of(pageResolver(page), 5), category);

        } else {
            articles = articleRepository
                    .findByCategoryOrderByIdDesc(
                            PageRequest.of(pageResolver(page), 5), category);
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

        return articleRepository.findArticleByIdFetchCategoryAndArticleTagLists(articleId);

    }

    public void editArticle(Long articleId, ArticleForm articleForm) {

        Article article = articleRepository.findById(articleId).get();
        Category category = categoryService.findCategory(articleForm.getCategory());
        tagsService.deleteArticleTags(article);
        tagsService.createNewTagsAndArticleTagList(articleForm.getTags(), article);
        articleForm.setThumbnailUrl(makeDefaultThumb(articleForm.getThumbnailUrl()));

        article.editArticle(articleForm,category);

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
                        .findAllByArticleTagsOrderById(PageRequest.of(pageResolver(page), 5), tag);

        return articles;

    }

    public Page<Article> getArticlesByKeyword(String keyword, Integer page) {

        Page<Article> articles =
                articleRepository
                        .findAllByKeywordOrderById(PageRequest.of(pageResolver(page),5), keyword);

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

    private int pageResolver(Integer rawPage) {
        if (rawPage == null || rawPage == 1) {
            return 0;
        } else return rawPage - 1;
    }

    private Article articleFrom(ArticleForm articleDto) {
        Member member =
                memberRepository.findById(articleDto.getMemberId()).orElseThrow(() -> {
                    throw new IllegalArgumentException("작성자를 확인할 수 없습니다");
                });

        return Article.builder()
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .toc(articleDto.getToc())
                .thumbnailUrl(makeDefaultThumb(articleDto.getThumbnailUrl()))
                .category(categoryService.findCategory(articleDto.getCategory()))
                .member(member)
                .build();
    }

}
