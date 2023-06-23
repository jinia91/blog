package kr.co.jiniaslog.blogcore.adapter.persistence.category

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryJpaRepository : JpaRepository<CategoryPM, Long> {
    fun findByLabel(label: String): CategoryPM?
}
