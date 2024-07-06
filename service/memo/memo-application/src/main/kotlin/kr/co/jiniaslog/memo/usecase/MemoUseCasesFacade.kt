package kr.co.jiniaslog.memo.usecase

interface MemoUseCasesFacade :
    IInitMemo,
    IUpdateMemoContents,
    IDeleteMemo,
    IMakeRelationShipFolderAndMemo,
    IUpdateMemoReferences
