package kr.co.jiniaslog.blog.domain.category

import io.kotest.matchers.shouldBe
import kr.co.jiniaslog.shared.CustomBehaviorSpec

class CategoryTests : CustomBehaviorSpec() {
    init {
        Given("부모 카테고리, 자식 카테고리들이 있고") {
            And("두 카테고리의 우선순위가 같고") {
                val parent =
                    Category.create(
                        id = CategoryId(1L),
                        name = CategoryName("parent"),
                        order = SortingOrder(1),
                        parentCategory = null,
                    )
                val child =
                    Category.create(
                        id = CategoryId(2L),
                        name = CategoryName("child"),
                        order = SortingOrder(1),
                        parentCategory = parent,
                    )
                When("부모 카테고리와 자식 카테고리를 비교하면") {
                    Then("부모 카테고리가 자식 카테고리보다 우선된다") {
                        parent.compareTo(child) shouldBe -1
                    }
                }
            }
            And("두 카테고리의 우선순위가 다르고") {
                val parent =
                    Category.create(
                        id = CategoryId(1L),
                        name = CategoryName("parent"),
                        order = SortingOrder(5),
                        parentCategory = null,
                    )
                val child =
                    Category.create(
                        id = CategoryId(2L),
                        name = CategoryName("child"),
                        order = SortingOrder(1),
                        parentCategory = parent,
                    )
                When("부모 카테고리와 자식 카테고리를 비교하면") {
                    Then("부모 카테고리가 자식 카테고리보다 우선된다") {
                        parent.compareTo(child) shouldBe -1
                    }
                }
            }

            And("부모 카테고리 2개 자식 카테고리 2개가 있고") {
                val parent1 =
                    Category.create(
                        id = CategoryId(1L),
                        name = CategoryName("parent"),
                        order = SortingOrder(5),
                        parentCategory = null,
                    )
                val parent2 =
                    Category.create(
                        id = CategoryId(2L),
                        name = CategoryName("parent"),
                        order = SortingOrder(1),
                        parentCategory = null,
                    )
                val child1 =
                    Category.create(
                        id = CategoryId(2L),
                        name = CategoryName("child"),
                        order = SortingOrder(1),
                        parentCategory = parent2,
                    )
                val child2 =
                    Category.create(
                        id = CategoryId(2L),
                        name = CategoryName("child"),
                        order = SortingOrder(3),
                        parentCategory = parent1,
                    )
                When("각 카테고리를 모두 비교해보면") {
                    Then("부모카테고리끼리 정렬된다") {
                        listOf(parent1, parent2).sorted() shouldBe listOf(parent2, parent1)
                    }

                    Then("자식카테고리끼리 정렬된다") {
                        listOf(child1, child2).sorted() shouldBe listOf(child1, child2)
                    }

                    Then("부모카테고리와 자식카테고리를 섞어도 정렬된다") {
                        listOf(parent1, parent2, child1, child2).sorted() shouldBe listOf(parent2, parent1, child1, child2)
                    }
                }
                When("계층별 정렬을 수행하면") {
                    val listByHierarchy = listOf(parent1, parent2, child1, child2).sortByHierarchy()
                    Then("트리로 정렬된다") {
                        listByHierarchy[0] shouldBe parent2
                        listByHierarchy[1] shouldBe child1
                        listByHierarchy[2] shouldBe parent1
                        listByHierarchy[3] shouldBe child2
                    }

                    And("자식 카테고리의 부모를 변경하면") {
                        child1.update(parentCategory = parent1)
                        val reorder = listByHierarchy.sortByHierarchy()
                        Then("트리로 정렬된다") {
                            reorder[0] shouldBe parent2
                            reorder[1] shouldBe parent1
                            reorder[2] shouldBe child1
                            reorder[3] shouldBe child2
                        }
                    }
                }
            }
        }
    }
}
