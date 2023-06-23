package kr.co.jiniaslog.blogcore.adapter.persistence.draft

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.persistence.BasePersistenceModel
import java.time.LocalDateTime

@Entity
@Table(
    name = "draft_articles",
)
class DraftArticlePM(
    @Id
    @Column(name = "draft_article_id")
    override val id: Long,

    @Column(nullable = false, name = "writer_id")
    var writerId: Long,

    @Column(nullable = true, length = 50, name = "title")
    var title: String?,

    @Column(nullable = true, length = 10000, name = "content")
    var content: String?,

    @Column(nullable = true, name = "thumbnail_url")
    var thumbnailUrl: String?,

    createdDate: LocalDateTime? = null,

    updatedDate: LocalDateTime? = null,
) : BasePersistenceModel(createdDate, updatedDate)
