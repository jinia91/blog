package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
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
            "where a.id =:id ")
    Article findArticleByIdFetchCategory(@Param("id") Long articleId);

}
