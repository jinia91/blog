package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.blog.domain.category.dto.CategoryDataHolder
import kr.co.jiniaslog.shared.core.annotation.DomainService

/**
 * 카테고리 동기화를 위한 도메인 서비스
 *
 * 그래프 구조화되어있는 카테고리 input 객체를 뎁스 - 정렬순서로 평탄화하여 업데이트 하기 편하게 반환한다
 *
 * - 삭제할 카테고리 목록
 * - 추가 및 수정할 카테고리 목록
 * 을 분리하여 반환한다.
 *
 */
@DomainService
class CategorySyncer {
    fun syncCategories(
        asIs: List<Category>,
        toBe: List<CategoryDataHolder>,
    ): SyncResult {
        val asIsMap = asIs.associateBy { it.entityId }
        val toBeUpsert = toBe.extractToBeUpsert(asIsMap)
        val toBeDelete = extractToBeDeleted(asIsMap, toBeUpsert.associateBy { it.entityId })
        return SyncResult(toBeDelete, toBeUpsert)
    }

    private fun List<CategoryDataHolder>.extractToBeUpsert(asIsMap: Map<CategoryId, Category>): List<Category> {
        val result = mutableListOf<Category>()

        fun refineAndTransformRecursively(categoryDto: CategoryDataHolder, parent: Category?) {
            val category = asIsMap[categoryDto.categoryId]?.also {
                it.sync(parent, categoryDto)
            } ?: categoryDto.toDomainEntity(parent)
            result.add(category)
            categoryDto.children.sortedBy { it.sortingPoint }
                .forEach { refineAndTransformRecursively(it, category) }
        }

        this.sortedBy { it.sortingPoint }
            .forEach { refineAndTransformRecursively(it, null) }

        return result
    }

    private fun CategoryDataHolder.toDomainEntity(parent: Category?): Category = Category(
        id = this.categoryId ?: CategoryId.newOne(),
        categoryTitle = categoryName,
        sortingPoint = sortingPoint,
    ).apply {
        changeParent(parent)
    }

    private fun Category.sync(
        parent: Category?,
        toUpdateData: CategoryDataHolder,
    ) {
        this.sync(
            categoryTitle = toUpdateData.categoryName,
            sortingPoint = toUpdateData.sortingPoint,
            parent = parent
        )
    }

    private fun extractToBeDeleted(
        asIsMap: Map<CategoryId, Category>,
        toBeMap: Map<CategoryId, Category>,
    ): List<Category> {
        val asIsIds = asIsMap.keys
        val toBeIds = toBeMap.keys
        val toBeDelete = (asIsIds - toBeIds).map { asIsMap.getValue(it) }
        return toBeDelete
    }
}

data class SyncResult(
    val toBeDelete: List<Category>,
    val toBeUpsert: List<Category>,
)
