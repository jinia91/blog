package kr.co.jiniaslog.blog.adapter.out.rdb.article

import kr.co.jiniaslog.blog.domain.article.Article
import kr.co.jiniaslog.blog.domain.article.ArticleStatus
import kr.co.jiniaslog.shared.adapter.out.rdb.AbstractPM
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "article")
class ArticlePM(
    @Id
    override val id: Long,
    val writerId: Long,
    var head: Long,
    var status: ArticleStatus,
    override var createdAt: LocalDateTime?,
    override var updatedAt: LocalDateTime?,
) : AbstractPM()

internal fun Article.toPM() =
    ArticlePM(
        id = this.id.value,
        writerId = this.writerId.value,
        head = this.head.value,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
