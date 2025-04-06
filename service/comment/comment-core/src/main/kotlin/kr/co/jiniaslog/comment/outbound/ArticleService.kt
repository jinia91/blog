package kr.co.jiniaslog.comment.outbound

interface ArticleService {
    fun isExist(refId: Long): Boolean
}
