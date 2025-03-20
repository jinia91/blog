package kr.co.jiniaslog.blog.usecase.article

interface ArticleUseCasesFacade :
    IStartToWriteNewDraftArticle,
    IUpdateDraftArticleContents,
    IAddAnyTagInArticle
