package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop6ByOrderByHitDesc();

    Slice<Article> findByOrderByIdDesc(Pageable pageable);

    @Query("select a " +
            "from Article a " +
            "inner join a.category c " +
            "where c.title=:category " +
            "order by a.id desc ")
    Slice<Article> findByCategoryOrderByIdDesc(Pageable pageable, @Param("category") String category);

    @Query("select a " +
            "from Article a " +
            "inner join a.category c " +
            "left join c.parents p " +
            "where p.title=:category " +
            "order by a.id desc ")
    Slice<Article> findByT1CategoryOrderByIdDesc(Pageable pageable, @Param("category") String category);

    Slice<Article> findAllByOrderByIdDesc(Pageable pageable);


    @Query("select a " +
            "from Article a " +
            "join fetch a.category " +
            "join fetch a.articleTagLists tl " +
            "join fetch tl.tags " +
            "where a.id =:id ")
    Article findArticleByIdFetchCategoryAndTags(@Param("id") Long articleId);

    @Query("select a " +
            "from Article a " +
            "join fetch a.category " +
            "join fetch a.articleTagLists " +
            "where a.id =:id ")
    Article findArticleByIdFetchCategoryAndArticleTagLists(@Param("id") Long articleId);

    List<Article> findTop6ByCategoryOrderByIdDesc(Category category);

    @Query("select a " +
            "from Article a " +
            "join a.articleTagLists at " +
            "join at.tags t " +
            "where t.name in :tag " +
            "order by a.id desc ")
    Page<Article> findAllByArticleTagsOrderById(Pageable pageable, @Param("tag") String tag);


    @Query("select a " +
            "from Article a " +
            "where a.title like %:keyword% " +
            "or a.content like %:keyword% " +
            "order by a.id desc ")
    Page<Article> findAllByKeywordOrderById(Pageable pageable, @Param("keyword") String keyword);



}
