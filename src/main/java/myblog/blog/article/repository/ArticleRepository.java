package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    /*
       - 최대 6개까지 조회수가 높은 게시물 가져오기
     */
    List<Article> findTop6ByOrderByHitDesc();

    /*
        - 카테고리별 최신 게시물 6개 가져오기
    */
    List<Article> findTop6ByCategoryOrderByIdDesc(Category category);

    /*
       - 전체게시물 최신순으로 오프셋 페이징처리해서 가져오기
     */
    Slice<Article> findByOrderByIdDesc(Pageable pageable);

    /*
       - 커서페이징으로 최신 게시물 가져오기
           - 첫번째 페이지용 쿼리
     */
    @Query("select a " +
            "from Article a " +
            "order by a.id desc ")
    List<Article> findByOrderByIdDescWithList(Pageable pageable);

    /*
       - 커서페이징으로 최신 게시물 가져오기
           - 커서 적용
     */
    @Query("select a " +
            "from Article a " +
            "where a.id < :articleId " +
            "order by a.id desc")
    List<Article> findByOrderByIdDesc(@Param("articleId") Long articleId,Pageable pageable);

    /*
        - 카테고리별(하위 카테고리) 페이징 처리해서 최신게시물순으로 Slice 가져오기
     */
    @Query("select a " +
            "from Article a " +
            "inner join a.category c " +
            "where c.title=:category " +
            "order by a.id desc ")
    Slice<Article> findBySubCategoryOrderByIdDesc(Pageable pageable, @Param("category") String category);

    /*
        - 카테고리별(상위 카테고리) 페이징 처리해서 최신게시물순으로 Slice 가져오기
            - 토탈카운트 쿼리 x
     */
    @Query("select a " +
            "from Article a " +
            "inner join a.category c " +
            "left join c.parents p " +
            "where p.title=:category " +
            "order by a.id desc ")
    Slice<Article> findBySupCategoryOrderByIdDesc(Pageable pageable, @Param("category") String category);

    /*
        - 카테고리와 태그를 모두 페치조인해서 아티클 조회
        - 아티클 세부 조회용도
    */
    @Query("select a " +
            "from Article a " +
            "join fetch a.category " +
            "join fetch a.articleTagLists tl " +
            "join fetch tl.tags " +
            "where a.id =:id ")
    Article findArticleByIdFetchCategoryAndTags(@Param("id") Long articleId);

    /*
        - 태그별 아티클 페이징 처리해서 조회
        - 토탈 카운트 쿼리 o
    */
    @Query("select a " +
            "from Article a " +
            "join a.articleTagLists at " +
            "join at.tags t " +
            "where t.name =:tag " +
            "order by a.id desc ")
    Page<Article> findAllByArticleTagsOrderById(Pageable pageable, @Param("tag") String tag);

    /*
        - 키워드별 아티클 페이징 처리해서 조회
        - 토탈 카운트 쿼리 o
        - 배포시 fts로 개선해보자
    */
    @Query("select a " +
            "from Article a " +
            "where a.title like %:keyword% " +
            "or a.content like %:keyword% " +
            "order by a.id desc ")
//    @Query(value = "select * " +
//            "from article " +
//            "where match(content, :keyword) " +
//            "order by article_id desc",nativeQuery = true)
    Page<Article> findAllByKeywordOrderById(Pageable pageable, @Param("keyword") String keyword);

    /*
        - 모든 게시물 조회
    */
    @EntityGraph(attributePaths = {"category"})
    List<Article> findAllByOrderByIdDesc();
}
