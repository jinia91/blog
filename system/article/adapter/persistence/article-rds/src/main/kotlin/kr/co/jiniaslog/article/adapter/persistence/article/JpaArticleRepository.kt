package kr.co.jiniaslog.article.adapter.persistence.article

import org.springframework.data.jpa.repository.JpaRepository

interface JpaArticleRepository : JpaRepository<ArticlePM, Long>
