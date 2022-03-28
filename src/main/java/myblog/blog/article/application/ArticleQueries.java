package myblog.blog.article.application;

import lombok.RequiredArgsConstructor;

import myblog.blog.article.application.port.incomming.response.ArticleResponseForCardBox;
import myblog.blog.article.application.port.incomming.ArticleQueriesUseCase;
import myblog.blog.article.application.port.outgoing.ArticleRepositoryPort;

import myblog.blog.article.domain.Article;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.category.domain.Category;
import myblog.blog.article.application.port.incomming.response.ArticleResponseByCategory;
import myblog.blog.article.application.port.incomming.response.ArticleResponseForDetail;
import myblog.blog.article.application.port.incomming.response.ArticleResponseForEdit;

import myblog.blog.shared.utils.MapperUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleQueries implements ArticleQueriesUseCase {

    private final ArticleRepositoryPort articleRepositoryPort;
    private final CategoryUseCase categoryUseCase;

    /*
        - 메인화면 위한 인기 아티클 6개 목록 가져오기
           - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
             카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
             DTO 매핑 로직 서비스단에서 처리
    */
    @Override
    @Cacheable(value = "layoutCaching", key = "1")
    public List<ArticleResponseForCardBox> getPopularArticles() {
        return articleRepositoryPort.findTop6ByOrderByHitDesc()
                .stream()
                .map(article -> MapperUtils.getModelMapper().map(article, ArticleResponseForCardBox.class))
                .collect(Collectors.toList());
    }
    /*
        - 메인화면 위한 최신 아티클 커서 페이징해서 가져오기
           - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
             카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
    */
    @Override
    @Cacheable(value = "layoutRecentArticleCaching", key = "#lastArticleId")
    public List<ArticleResponseForCardBox> getRecentArticles(Long lastArticleId) {
        List<Article> articles = lastArticleId.equals(0L) ?
                articleRepositoryPort
                        .findByOrderByIdDescWithList(PageRequest.of(0, 5))
                :
                articleRepositoryPort
                        .findByOrderByIdDesc(lastArticleId, PageRequest.of(0, 5));
        return articles
                .stream()
                .map(article -> MapperUtils.getModelMapper().map(article, ArticleResponseForCardBox.class))
                .collect(Collectors.toList());
    }
    /*
        - 카테고리별 게시물 페이징 처리해서 가져오기
        - tier 1은 super / tier 2는 sub
    */
    @Override
    public Slice<ArticleResponseForCardBox> getArticlesByCategory(String category, Integer tier, Integer page) {
        Slice<Article> articles = null;

        if (tier.equals(0)) {
            articles = articleRepositoryPort
                    .findByOrderByIdDesc(
                            PageRequest.of(pageResolve(page), 5));
        }
        if (tier.equals(1)) {
            articles = articleRepositoryPort
                    .findBySuperCategoryOrderByIdDesc(
                            PageRequest.of(pageResolve(page), 5), category);
        }
        if (tier.equals(2)) {
            articles = articleRepositoryPort
                    .findBySubCategoryOrderByIdDesc(
                            PageRequest.of(pageResolve(page), 5), category);
        }

        if(articles == null) throw new IllegalArgumentException("NotFoundArticleException");

        return articles.map(article -> MapperUtils.getModelMapper().map(article, ArticleResponseForCardBox.class));
    }

    /*
        - 아티클 수정을 위한 반환
    */
    @Override
    public ArticleResponseForEdit getArticleForEdit(Long id){
        Article article = articleRepositoryPort.findArticleByIdFetchCategoryAndTags(id);
        ArticleResponseForEdit articleDto = MapperUtils.getModelMapper().map(article, ArticleResponseForEdit.class);
        List<String> articleTagStrings = article.getArticleTagLists()
                .stream()
                .map(articleTag -> articleTag.getTags().getName())
                .collect(Collectors.toList());
        articleDto.setArticleTagList(articleTagStrings);
        return articleDto;
    }
    /*
    *  - 아티클 상세 조회를 위한 쿼리
    * */
    @Override
    public ArticleResponseForDetail getArticleForDetail(Long id){
        Article article = articleRepositoryPort.findArticleByIdFetchCategoryAndTags(id);
        ArticleResponseForDetail articleResponseForDetail =
                MapperUtils.getModelMapper().map(article, ArticleResponseForDetail.class);

        List<String> tags =
                article.getArticleTagLists()
                        .stream()
                        .map(tag -> tag.getTags().getName())
                        .collect(Collectors.toList());

        articleResponseForDetail.setTags(tags);
        return articleResponseForDetail;
    }

    /*
        - 카테고리별 최신게시물 6개만 아티클 상세뷰 위해 가져오는로직
    */
    @Override
    public List<ArticleResponseByCategory> getArticlesByCategoryForDetailView(String categoryName){
        Category category = categoryUseCase.findCategory(categoryName);
        return articleRepositoryPort.findTop6ByCategoryOrderByIdDesc(category)
                .stream()
                .map(article -> MapperUtils.getModelMapper().map(article, ArticleResponseByCategory.class))
                .collect(Collectors.toList());
    }
    /*
        - 태그별 게시물 페이징 처리해서 가져오기
    */
    @Override
    public Page<ArticleResponseForCardBox> getArticlesByTag(String tag, Integer page) {
        return articleRepositoryPort
                .findAllByArticleTagsOrderById(PageRequest.of(pageResolve(page), 5), tag)
                .map(article ->
                        MapperUtils.getModelMapper().map(article, ArticleResponseForCardBox.class));
    }
    /*
        - 검색어별 게시물 페이징 처리해서 가져오기
    */
    @Override
    public Page<ArticleResponseForCardBox> getArticlesByKeyword(String keyword, Integer page) {
        return articleRepositoryPort
                .findAllByKeywordOrderById(PageRequest.of(pageResolve(page),5), keyword)
                .map(article ->
                        MapperUtils.getModelMapper().map(article, ArticleResponseForCardBox.class));
    }
    /*
        - 페이지 시작점 0~1변경 메서드
    */
    private int pageResolve(Integer rawPage) {
        if (rawPage == null || rawPage == 1) {
            return 0;
        } else return rawPage - 1;
    }
}
