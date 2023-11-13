package kr.co.jiniaslog.blog.adapter.out.rdb.article

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ArticleCommitRdbRepository : CoroutineCrudRepository<ArticleCommitPM, Long> {
    suspend fun findAllByArticleId(articleId: Long): List<ArticleCommitPM>

    suspend fun deleteAllByArticleId(articleId: Long)
}
