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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
            - 글작성시 아티클 캐싱 초기화
    */
    @CacheEvict(value = {"layoutCaching", "layoutRecentArticleCaching","seoCaching"}, allEntries = true)
    public Long writeArticle(ArticleForm articleDto, Member writer) {
        Article newArticle = articleFrom(articleDto, writer);
        articleRepository.save(newArticle);
        tagsService.createNewTagsAndArticleTagList(articleDto.getTags(), newArticle);
        return newArticle.getId();
    }

    /*
        - 아티클 수정 로직
            - 글 수정시 아티클 캐싱 초기화
    */
    @CacheEvict(value = {"layoutCaching", "layoutRecentArticleCaching","seoCaching"}, allEntries = true)
    public void editArticle(Long articleId, ArticleForm articleForm) {
        Article article = articleRepository.findById(articleId).get();
        Category category = categoryService.findCategory(articleForm.getCategory());
        tagsService.deleteAllTagsWith(article);
        tagsService.createNewTagsAndArticleTagList(articleForm.getTags(), article);
        article.isEditedFrom(articleForm,category);
    }

    /*
        - 아티클 삭제 로직
            - 글 삭제시 아티클 캐싱 초기화
    */
    @CacheEvict(value = {"layoutCaching", "layoutRecentArticleCaching","seoCaching"}, allEntries = true)
    public void deleteArticle(Long articleId) {
        naArticleRepository.deleteArticle(articleId);
    }

    /*
        - 메인화면 위한 인기 아티클 6개 목록 가져오기
           - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
             카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
             DTO 매핑 로직 서비스단에서 처리
    */
    @Cacheable(value = "layoutCaching", key = "1")
    public List<Article> getPopularArticles() {
        return articleRepository.findTop6ByOrderByHitDesc();
    }

    /*
        - 메인화면 위한 최신 아티클 커서 페이징해서 가져오기
           - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
             카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
    */
    @Cacheable(value = "layoutRecentArticleCaching", key = "#lastArticleId")
    public List<Article> getRecentArticles(Long lastArticleId) {
        return lastArticleId.equals(0L)?
                articleRepository
                        .findByOrderByIdDescWithList(PageRequest.of(0, 5))
                :
                articleRepository
                        .findByOrderByIdDesc(lastArticleId, PageRequest.of(0, 5));

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
        }

        if (tier.equals(1)) {
            articles = articleRepository
                    .findBySupCategoryOrderByIdDesc(
                            PageRequest.of(pageResolve(page), 5), category);

        }

        if (tier.equals(2)) {
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
        - 모든 게시물 조회
    */
    public List<Article> getTotalArticle(){
        return articleRepository.findAllByOrderByIdDesc();
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
        - 아티클 조회수 추가
    */
    public void addHit(Article article) {
        article.addHit();
    }

    /*
        - 깃헙에 아티클 푸시하기
    */
    public void pushArticleToGithub(Long articleId) {
        articleRepository.findById(articleId).ifPresent(this::pushArticleToGithub);
    }

    private void pushArticleToGithub(Article article) {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(gitToken).build();
            GHRepository repository = gitHub.getRepository(gitRepo);
            repository.createContent()
                    .path(article.getCategory().getParents().getTitle()+"/"+ article.getCategory().getTitle()+"/"+ article.getTitle()+".md")
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
