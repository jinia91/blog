package kr.co.jiniaslog.memo.usecase

interface FolderUseCasesFacade :
    ICreateNewFolder,
    IChangeFolderName,
    IMakeRelationShipFolderAndFolder,
    IDeleteFoldersRecursively,
    IDeleteAllWithoutAdmin,
    IReorderFolder
