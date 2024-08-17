package kr.co.jiniaslog.utils

/**
 * 데이터베이스 초기화 컴포넌트
 *
 * 구현체에 맞는 데이터베이스 초기화를 수행한다
 *
 */
interface DbCleaner {
    fun tearDownAll()
}
