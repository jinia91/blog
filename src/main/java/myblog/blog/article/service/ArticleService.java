package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import myblog.blog.member.doamin.Member;
import myblog.blog.article.dto.ArticleForm;
import myblog.blog.article.repository.ArticleRepository;
import myblog.blog.article.repository.NaArticleRepository;
import myblog.blog.category.service.CategoryService;
import myblog.blog.tags.service.TagsService;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.List;

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

    /*
        - 아티클 작성 로직
    */
    public Long writeArticle(ArticleForm articleDto, Member writer) {

        Article newArticle = articleFrom(articleDto, writer);
        articleRepository.save(newArticle);
        tagsService.createNewTagsAndArticleTagList(articleDto.getTags(), newArticle);

        return newArticle.getId();
    }

    /*
        - 아티클 수정 로직
    */
    public void editArticle(Long articleId, ArticleForm articleForm) {

        Article article = articleRepository.findById(articleId).get();
        Category category = categoryService.findCategory(articleForm.getCategory());
        tagsService.deleteArticleTags(article);
        tagsService.createNewTagsAndArticleTagList(articleForm.getTags(), article);

        // 더티 체킹으로 업데이트
        article.editArticle(articleForm,category);
    }

    /*
        - 메인화면 위한 인기 아티클 6개 목록 가져오기
    */
    public List<Article> getPopularArticles() {

        return articleRepository.findTop6ByOrderByHitDesc();

    }

    /*
        - 메인화면 위한 최신 아티클 페이징처리해서 가져오기
    */
    public Slice<Article> getRecentArticles(int page) {
        return articleRepository
                .findByOrderByIdDesc(PageRequest.of(page, 5));
    }

    /*
        - 카테고리별 게시물 페이징 처리해서 가져오기
    */
    public Slice<Article> getArticlesByCategory(String category, Integer tier, Integer page) {

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

        return articles;
    }

    /*
        - 아티클 읽기 위한 페치로 전체 가져오기
    */
    public Article readArticle(Long id){
         return articleRepository.findArticleByIdFetchCategoryAndTags(id);
    }
    /*
        - 아티클 삭제 로직
    */
    public void deleteArticle(Long articleId) {

          naArticleRepository.deleteArticle(articleId);
    }

    /*
        - 카테고리별 최신게시물 6개만 아티클 상세뷰 위해 가져오는로직
    */
    public List<Article> getArticlesByCategoryForDetailView(Category category){

        return articleRepository.findTop6ByCategoryOrderByIdDesc(category);

    }

    /*
        - 태그별 게시물 페이징 처리해서 가져오기
    */
    public Page<Article> getArticlesByTag(String tag, Integer page) {

        return articleRepository
                .findAllByArticleTagsOrderById(PageRequest.of(pageResolve(page), 5), tag);

    }

    /*
        - 검색어별 게시물 페이징 처리해서 가져오기
    */
    public Page<Article> getArticlesByKeyword(String keyword, Integer page) {

        return articleRepository
                .findAllByKeywordOrderById(PageRequest.of(pageResolve(page),5), keyword);
    }

    /*
        - 깃헙에 아티클 푸시하기
    */
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

    /*
        - 페이지 시작점 0~1변경 메서드
    */
    private int pageResolve(Integer rawPage) {
        if (rawPage == null || rawPage == 1) {
            return 0;
        } else return rawPage - 1;
    }

    /*
        - 새로운 아티클 도메인 생성 메서드
    */
    private Article articleFrom(ArticleForm articleDto, Member writer) {

        return Article.builder()
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .toc(articleDto.getToc())
                .member(writer)
                .thumbnailUrl(articleDto.getThumbnailUrl())
                .category(categoryService.findCategory(articleDto.getCategory()))
                .build();
    }

}
