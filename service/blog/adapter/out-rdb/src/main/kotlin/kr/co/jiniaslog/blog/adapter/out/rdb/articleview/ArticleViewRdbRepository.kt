package kr.co.jiniaslog.blog.adapter.out.rdb.articleview

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ArticleViewRdbRepository : CoroutineCrudRepository<ArticleViewPM, Long>
