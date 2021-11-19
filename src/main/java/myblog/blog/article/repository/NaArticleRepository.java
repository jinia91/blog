package myblog.blog.article.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NaArticleRepository {


    @Delete("delete from article " +
            "where article_id = #{articleId} ")
    void deleteArticle(Long articleId);
}
