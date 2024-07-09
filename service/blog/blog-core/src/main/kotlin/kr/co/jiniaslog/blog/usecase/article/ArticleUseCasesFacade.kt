package kr.co.jiniaslog.blog.usecase.article

interface ArticleUseCasesFacade :
    IStartToWriteNewDraftArticle,
    IPublishArticle,
    IDeleteArticle,
    IUnDeleteArticle
