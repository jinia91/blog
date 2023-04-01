package kr.co.jiniaslog.article.adapter.persistence.article

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.persistence.BasePM

@Entity
@Table(indexes = [Index(name = "i_article_title", columnList = "title")])
class ArticlePM(
    @Id
    @Column(name = "article_id")
    val id: Long? = null,

    @Column(nullable = false, length = 50)
    var title: String? = null,

    @Column(nullable = false, length = 10000)
    var content: String? = null,

    @Column(columnDefinition = "bigint default 0", nullable = false)
    var hit: Long? = null,

    @Column(nullable = false)
    var thumbnailUrl: String? = null,

    @Column(nullable = false)
    var memberId: Long? = null,
) : BasePM()
