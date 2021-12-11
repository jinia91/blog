package myblog.blog.article.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NaArticleRepository {

    /*
        - 삭제처리시 불필요한 조회방지 위해 네이티브 쿼리 사용 cascade delete 처리
    */
    @Delete("delete from article " +
            "where article_id = #{articleId} ")
    void deleteArticle(Long articleId);

}
