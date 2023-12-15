package kr.co.jiniaslog.memo.queries

import kr.co.jiniaslog.memo.queries.model.FolderInfo

interface IGetFoldersAll {
    fun handle(query: Query): Info

    class Query()

    data class Info(val folderInfos: List<FolderInfo>)
}
