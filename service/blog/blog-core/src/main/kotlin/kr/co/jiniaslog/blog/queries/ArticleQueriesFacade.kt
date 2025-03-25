package kr.co.jiniaslog.blog.queries

interface ArticleQueriesFacade :
    IGetArticleById,
    IGetPublishedSimpleArticleListWithCursor,
    IGetPublishedArticleByKeyword
