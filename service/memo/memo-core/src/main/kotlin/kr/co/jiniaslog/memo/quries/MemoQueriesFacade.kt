package kr.co.jiniaslog.memo.quries

interface MemoQueriesFacade :
    IRecommendRelatedMemo,
    IGetMemoById,
    IGetAllReferencesByMemo,
    IGetAllReferencedByMemo,
    ICheckMemoExisted
