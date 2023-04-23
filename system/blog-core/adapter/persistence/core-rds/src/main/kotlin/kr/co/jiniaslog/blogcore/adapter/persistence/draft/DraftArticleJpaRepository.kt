package kr.co.jiniaslog.blogcore.adapter.persistence.draft

import org.springframework.data.jpa.repository.JpaRepository

interface DraftArticleJpaRepository : JpaRepository<DraftArticlePM, Long>
