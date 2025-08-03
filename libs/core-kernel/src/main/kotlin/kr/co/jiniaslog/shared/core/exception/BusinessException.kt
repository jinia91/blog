package kr.co.jiniaslog.shared.core.exception

import java.lang.RuntimeException

/**
 * 비즈니스 로직상에서 발생하는 백엔드 시스템상의 복구할수 없는 예외에 대한 추상 클래스
 *
 * @property errorMessage 예외 발생시 메시지
 * @property shouldLog 예외 발생시 로그를 남길지 여부
 */
abstract class BusinessException(
    open val errorMessage: String,
    open val shouldLog: Boolean,
) : RuntimeException()

abstract class ResourceNotFoundException(
    override val errorMessage: String,
    override val shouldLog: Boolean,
) : BusinessException(errorMessage, shouldLog)

abstract class ResourceAlreadyExistsException(
    override val errorMessage: String,
    override val shouldLog: Boolean,
) : BusinessException(errorMessage, shouldLog)
