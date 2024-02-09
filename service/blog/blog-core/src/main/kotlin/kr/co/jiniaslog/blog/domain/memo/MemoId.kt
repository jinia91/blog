package kr.co.jiniaslog.blog.domain.memo

import kr.co.jiniaslog.shared.core.domain.vo.ValueObject

/*

kotlin value class compile 버그(?)로 프리미티브 타입을 감싼 value class 는
nullable 사용시 value class 타입이 언박싱 되지 않고 value class 타입 그대로 사용되는 버그가 있다.

리플렉션같은 타입선언을 사용하는 경우에는 타입을 인지하지 못하는 이슈가 존재
converter를 사용해서 임시 해결
 */
@JvmInline
value class MemoId(val value: Long) : ValueObject {
    override fun validate() {
        require(value > 0) { "id must be positive" }
    }
}
