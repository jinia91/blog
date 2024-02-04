package kr.co.jiniaslog.blog.domain.tag

@JvmInline
value class TagName(val value: String) {
    private fun validate() {
        require(value.length <= 20) { "태그 이름은 20자를 넘을 수 없습니다." }
    }

    companion object {
        val EMPTY = TagName("")
    }
}
