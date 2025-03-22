package kr.co.jiniaslog.memo.adapter.outbound.mysql

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

@Mapper
interface MemoFolderMapper {

    @Select(
        """
    WITH RECURSIVE recursive_folder(id) AS (
        SELECT id FROM jiniaslog_memo.folder WHERE id = #{rootId}
        UNION ALL
        SELECT f.id FROM jiniaslog_memo.folder f
        JOIN recursive_folder rf ON f.parent_id = rf.id
    )
    SELECT id FROM recursive_folder
"""
    )
    fun findRecursiveFolderIds(@Param("rootId") rootId: Long): List<Long>
}
