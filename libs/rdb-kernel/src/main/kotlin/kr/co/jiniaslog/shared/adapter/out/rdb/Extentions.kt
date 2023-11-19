package kr.co.jiniaslog.shared.adapter.out.rdb

import kr.co.jiniaslog.shared.core.domain.DomainEntity
import kr.co.jiniaslog.shared.core.domain.ValueObject

fun <T : ValueObject> DomainEntity<T>.isNew(): Boolean = createdAt == null
