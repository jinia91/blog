package kr.co.jiniaslog.article.adapter.persistence.article

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(indexes = [Index(name = "i_article_title", columnList = "title")])
class ArticlePM(
    @Id
    @Column(name = "article_id")
    private val id: Long? = null,

    @Column(nullable = false, length = 50)
    private var title: String? = null,

    @Column(nullable = false, length = 10000)
    private var content: String? = null,

    @Column(columnDefinition = "bigint default 0", nullable = false)
    private var hit: Long? = null,

    @Column(nullable = false)
    private var thumbnailUrl: String? = null,

    @Column(nullable = false)
    private var memberId: Long? = null,
)
