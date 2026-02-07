package kr.co.jiniaslog.memo.usecase

interface MemoUseCasesFacade :
    IInitMemo,
    ICreateMemoWithContent,
    IUpdateMemoContents,
    IDeleteMemo,
    IMakeRelationShipFolderAndMemo,
    IUpdateMemoReferences,
    IReorderMemo
