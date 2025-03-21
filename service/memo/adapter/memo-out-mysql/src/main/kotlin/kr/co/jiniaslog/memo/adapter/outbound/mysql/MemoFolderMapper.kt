package kr.co.jiniaslog.memo.adapter.outbound.mysql

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Update

@Mapper
interface MemoFolderMapper {

    @Update(
        """
        CREATE TEMPORARY TABLE IF NOT EXISTS tmp_recursive_folder AS
        WITH RECURSIVE recursive_folder(id) AS (
            SELECT id FROM jiniaslog_memo.folder WHERE id = #{rootId}
            UNION ALL
            SELECT f.id FROM jiniaslog_memo.folder f
            JOIN recursive_folder rf ON f.parent_id = rf.id
        )
        SELECT id FROM recursive_folder
    """
    )
    fun createRecursiveFolderTable(@Param("rootId") rootId: Long)

    @Update(
        """
        CREATE TEMPORARY TABLE IF NOT EXISTS tmp_memo_ids AS
        SELECT m.id FROM jiniaslog_memo.memo m
        JOIN tmp_recursive_folder rf ON m.parent_folder_id = rf.id
    """
    )
    fun createMemoIdTable()

    @Update(
        """
        DELETE FROM jiniaslog_memo.memo_reference
        WHERE Memo_id IN (SELECT id FROM tmp_memo_ids)
    """
    )
    fun deleteMemoReferences()

    @Update(
        """
        DELETE FROM jiniaslog_memo.memo
        WHERE id IN (SELECT id FROM tmp_memo_ids)
    """
    )
    fun deleteMemos()

    @Update(
        """
        DELETE FROM jiniaslog_memo.folder
        WHERE id IN (SELECT id FROM tmp_recursive_folder)
    """
    )
    fun deleteFolders()
}
