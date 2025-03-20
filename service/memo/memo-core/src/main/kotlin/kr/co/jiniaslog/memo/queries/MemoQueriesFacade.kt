package kr.co.jiniaslog.memo.queries

interface MemoQueriesFacade :
    IRecommendRelatedMemo,
    IGetMemoById,
    IGetAllReferencesByMemo,
    IGetAllReferencedByMemo,
    ICheckMemoExisted,
    ISearchAllMemoByKeyword
