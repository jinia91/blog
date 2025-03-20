package kr.co.jiniaslog.shared.adapter.out.rdb

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import kr.co.jiniaslog.shared.core.domain.AggregateRoot
import kr.co.jiniaslog.shared.core.domain.vo.ValueObject
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime

/**
 * JPA용 AggregateRoot
 *
 * 영속성 모델을 분리하지 않고 도메인 모델을 사용할 경우 JPA의 Audit기능과의 충돌 방지를 위해 사용
 *
 * @see AuditingAggregateListener
 */
@EntityListeners(AuditingAggregateListener::class)
@MappedSuperclass
abstract class JpaAggregate<T : ValueObject> : AggregateRoot<T>(), Persistable<T> {
    @Column(name = "created_at", nullable = false, updatable = false)
    override var createdAt: LocalDateTime? = null

    @Column(name = "updated_at", nullable = false)
    override var updatedAt: LocalDateTime? = null

    override fun isNew(): Boolean = createdAt == null

    override fun getId(): T = entityId
}
