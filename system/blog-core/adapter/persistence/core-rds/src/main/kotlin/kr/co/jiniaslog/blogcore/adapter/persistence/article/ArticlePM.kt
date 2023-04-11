package kr.co.jiniaslog.blogcore.adapter.persistence.article

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.persistence.BasePM

@Entity
@Table(
    name = "article",
)
class ArticlePM(
    @Id
    @Column(name = "article_id")
    val id: Long,

    @Column(nullable = false, length = 50, name = "title")
    var title: String,

    @Column(nullable = false, length = 10000, name = "content")
    var content: String,

    @Column(columnDefinition = "bigint default 0", nullable = false, name = "hit")
    var hit: Long,

    @Column(nullable = false, name = "thumbnail_url")
    var thumbnailUrl: String,

    @Column(nullable = false, name = "writer_id")
    var writerId: Long,
) : BasePM()
