package kr.co.jiniaslog.blog.domain.category


data class SimpleCategoryVo(
    val categoryId: CategoryId?,
    val categoryName: CategoryTitle,
    val children : List<SimpleCategoryVo> = listOf(),
    val depth: Int,
    val sortingPoint: Int,
) : Comparable<SimpleCategoryVo> {
    override fun compareTo(other: SimpleCategoryVo): Int {
        return this.sortingPoint.compareTo(other.sortingPoint)
    }
}
