package kr.co.jiniaslog.memo.queries

interface MemoQueriesFacade :
    IGetAllMemos,
    IRecommendRelatedMemo,
    IGetMemoById,
    IGetAllReferencesByMemo,
    IGetAllReferencedByMemo,
    ICheckMemoExisted
