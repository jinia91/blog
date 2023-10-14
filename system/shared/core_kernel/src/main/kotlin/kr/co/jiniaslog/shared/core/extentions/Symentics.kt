package kr.co.jiniaslog.shared.core.extentions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun Long?.isNullOrZero(): Boolean {
    contract {
        returns(false) implies (this@isNullOrZero != null)
    }

    return this == null || this == 0L
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }

    return java.util.Objects.isNull(this)
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
    contract {
        returns(false) implies (this@isNotNull != null)
    }

    return java.util.Objects.nonNull(this)
}
