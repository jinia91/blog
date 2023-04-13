package kr.co.jiniaslog.user.adapter.persistence.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.co.jiniaslog.shared.persistence.BasePM

@Entity
@Table(
    name = "user",
)
class UserPM(
    @Id
    @Column(name = "user_id")
    override val id: Long,
) : BasePM()
