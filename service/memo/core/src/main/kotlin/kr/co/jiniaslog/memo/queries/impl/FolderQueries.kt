package kr.co.jiniaslog.memo.queries.impl

import kr.co.jiniaslog.memo.queries.IGetFoldersAll
import kr.co.jiniaslog.shared.core.annotation.UseCaseInteractor

interface FolderQueriesFacade :
    IGetFoldersAll

@UseCaseInteractor
internal class FolderQueries(
    private val queries: FolderAndMemoQueries,
) : FolderQueriesFacade {
    override fun handle(query: IGetFoldersAll.Query): IGetFoldersAll.Info {
        return queries.getFoldersAll()
    }
}

interface FolderAndMemoQueries {
    fun getFoldersAll(): IGetFoldersAll.Info
}
