package kr.co.jiniaslog.blog.adapter.out.rdb.category

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CategoryRdbRepository : CoroutineCrudRepository<CategoryPM, Long>
