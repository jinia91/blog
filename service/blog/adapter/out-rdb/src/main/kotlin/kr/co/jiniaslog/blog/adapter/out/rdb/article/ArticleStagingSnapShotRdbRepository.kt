package kr.co.jiniaslog.blog.adapter.out.rdb.article

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ArticleStagingSnapShotRdbRepository : CoroutineCrudRepository<ArticleStagingSnapShotPM, Long>