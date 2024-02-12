package kr.co.jiniaslog.blog.domain.category

import kr.co.jiniaslog.shared.core.annotation.DomainService
import kr.co.jiniaslog.shared.core.domain.IdUtils
import java.util.LinkedList
import java.util.Queue

@DomainService
class CategorySyncer {
    fun syncCategories(
        asIs: List<Category>,
        toBe: List<SimpleCategoryVo>,
    ): SyncResult {
        val sortedToBe = toBe.flattenAndSorted()

        val asIsMap = asIs.associateBy { it.id }
        val toBeMap =
            sortedToBe
                .associateBy { it.categoryId ?: CategoryId(IdUtils.generate()) }

        val toBeDelete = getToBeDeleted(toBeMap, asIsMap)
        val toBeUpsert: List<Category> = getToBeUpsert(toBeMap, asIsMap)

        return SyncResult(toBeDelete, toBeUpsert)
    }

    private fun List<SimpleCategoryVo>.flattenAndSorted(): List<SimpleCategoryVo> {
        val result = mutableListOf<SimpleCategoryVo>()

        fun flattenAndSortRecursively(category: SimpleCategoryVo) {
            result.add(category)
            category.children.sortedBy { it.sortingPoint }.forEach { flattenAndSortRecursively(it) }
        }

        this.sortedBy { it.sortingPoint }
            .forEach { flattenAndSortRecursively(it) }

        return result
    }

    private fun getToBeDeleted(
        toBeMap: Map<CategoryId, SimpleCategoryVo>,
        asIsMap: Map<CategoryId, Category>,
    ): List<Category> {
        val toBeIds = toBeMap.keys
        val asIsIds = asIsMap.keys

        val toDelete = asIsIds - toBeIds
        val toBeDelete = toDelete.map { asIsMap.getValue(it) }
        return toBeDelete
    }

    private fun getToBeUpsert(
        toBeMap: Map<CategoryId, SimpleCategoryVo>,
        asIsMap: Map<CategoryId, Category>,
    ): List<Category> {
        val queue =
            toBeMap.map { (categoryId, categoryVo) ->
                categoryId to categoryVo
            }.toCollection(LinkedList())

        val result = mutableListOf<Category>()
        getToBeUpsertRecursively(
            queue,
            asIsMap,
            0,
            null,
            result,
        )
        return result
    }

    private fun getToBeUpsertRecursively(
        queue: Queue<Pair<CategoryId, SimpleCategoryVo>>,
        asIsMap: Map<CategoryId, Category>,
        depth: Int,
        parent: Category?,
        result: MutableList<Category>,
    ) {
        while (queue.isNotEmpty()) {
            val peeked = queue.peek().second
            val needToMoveDepth = peeked.depth != depth
            if (needToMoveDepth) {
                if (depth > peeked.depth) return
                getToBeUpsertRecursively(queue, asIsMap, depth + 1, result.last(), result)
            } else {
                val (categoryId, categoryVO) = queue.poll()
                val category = upsert(asIsMap, categoryId, categoryVO, parent, depth)
                result.add(category)
            }
        }
        return
    }

    private fun upsert(
        asIsMap: Map<CategoryId, Category>,
        categoryId: CategoryId,
        categoryVO: SimpleCategoryVo,
        parent: Category?,
        depth: Int,
    ): Category {
        val category =
            asIsMap[categoryId]?.apply {
                this.edit(
                    categoryTitle = categoryVO.categoryName,
                    depth = depth,
                    sortingPoint = categoryVO.sortingPoint,
                )
            } ?: categoryVO.toEntity(categoryId)

        parent?.let {
            category.setParent(parent)
        }

        return category
    }

    private fun SimpleCategoryVo.toEntity(categoryId: CategoryId) =
        Category(
            categoryId = categoryId,
            categoryTitle = this.categoryName,
            depth = this.depth,
            sortingPoint = this.sortingPoint,
        )
}

data class SyncResult(
    val toBeDelete: List<Category>,
    val toBeUpsert: List<Category>,
)
