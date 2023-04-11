package kr.co.jiniaslog.shared.core.extentions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <reified T : Exception> shouldBe(value: Boolean, lazyMessage: () -> Any) {
    contract {
        returns() implies value
    }
    if (!value) {
        val message = lazyMessage()
        val exception = T::class.java.getConstructor(String::class.java).newInstance(message.toString())
        throw exception
    }
}
