package myblog.blog.article.adapter.outgoing.persistence;

import myblog.blog.article.domain.Article;
import myblog.blog.article.domain.ArticleTagList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaArticleTagListsRepository extends JpaRepository<ArticleTagList, Long> {

    /*
        - 아티클 연관 태그 삭제 쿼리
            - cascade 필요시에는 아티클 삭제로 일괄 삭제하므로 해당쿼리는 연관태그 수정용
    */
    @Modifying
    @Query("delete from ArticleTagList t " +
            "where t.article =:article")
    void deleteByArticle(@Param("article") Article article);
}
