package myblog.blog.tags.repository;

import myblog.blog.article.domain.Article;
import myblog.blog.tags.domain.ArticleTagList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ArticleTagListsRepository extends JpaRepository<ArticleTagList, Long> {


    @Transactional
    @Modifying
    @Query("delete from ArticleTagList t " +
            "where t.article =:article")
    void deleteByArticle(@Param("article") Article article);


}
