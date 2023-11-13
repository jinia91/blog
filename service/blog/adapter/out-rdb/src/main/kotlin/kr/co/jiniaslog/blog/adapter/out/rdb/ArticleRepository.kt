package kr.co.jiniaslog.blog.adapter.out.rdb

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

internal interface ArticleRdbRepository : CoroutineCrudRepository<ArticlePm, Long>

@Table(name = "sample")
class ArticlePm(
    @Id
    val id: Long,
) : Persistable<Long> {
    override fun getId(): Long? {
        return id
    }

    override fun isNew(): Boolean = true
}
