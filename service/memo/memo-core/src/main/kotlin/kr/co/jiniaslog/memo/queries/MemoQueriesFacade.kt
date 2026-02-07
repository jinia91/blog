package kr.co.jiniaslog.memo.queries

interface MemoQueriesFacade :
    IRecommendRelatedMemo,
    IGetMemoById,
    IGetAllMemosByAuthorId,
    IGetAllReferencesByMemo,
    IGetAllReferencedByMemo,
    ICheckMemoExisted,
    ISearchAllMemoByKeyword
