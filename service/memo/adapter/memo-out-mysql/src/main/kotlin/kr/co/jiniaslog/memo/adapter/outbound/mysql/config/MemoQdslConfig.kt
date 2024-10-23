package kr.co.jiniaslog.memo.adapter.outbound.mysql.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MemoQdslConfig {
    @PersistenceContext(name = "memoEntityManager")
    lateinit var memoEntityManager: EntityManager

    @Bean
    fun memoJpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(memoEntityManager)
    }
}
