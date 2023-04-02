package kr.co.jiniaslog.article.adapter.http.adapter.persistence.article

import org.springframework.data.jpa.repository.JpaRepository

interface JpaArticleRepository : JpaRepository<ArticlePM, Long>
