package kr.co.jiniaslog.shared.core.extentions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun Long?.isNullOrZero(): Boolean {
    contract {
        returns(false) implies (this@isNullOrZero != null)
    }

    return this == null || this == 0L
}
