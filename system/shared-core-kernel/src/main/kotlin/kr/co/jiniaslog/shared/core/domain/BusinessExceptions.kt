package kr.co.jiniaslog.shared.core.domain

sealed class BusinessException : RuntimeException {
    constructor()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace,
    )
    abstract val isNecessaryToLog: Boolean
}

open class ValidationException(message: String? = null, cause: Throwable? = null) :
    BusinessException(message, cause) {
    override val isNecessaryToLog: Boolean
        get() = false
}

open class ResourceNotFoundException(message: String? = null, cause: Throwable? = null) :
    BusinessException(message, cause) {
    override val isNecessaryToLog: Boolean
        get() = true
}

open class ResourceConflictException(message: String? = null, cause: Throwable? = null) :
    BusinessException(message, cause) {
    override val isNecessaryToLog: Boolean
        get() = true
}
