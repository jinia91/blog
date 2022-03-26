package myblog.blog.comment.adapter.outgoing.persistence;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MybatisCommentRepository {

    /*
        - cascade 삭제처리
    */
    @Delete("delete from comment " +
            "where comment_id = #{commentId} ")
    void deleteComment(Long commentId);

}
