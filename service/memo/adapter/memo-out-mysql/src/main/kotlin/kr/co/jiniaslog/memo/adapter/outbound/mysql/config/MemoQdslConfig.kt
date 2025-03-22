package kr.co.jiniaslog.memo.adapter.outbound.mysql.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@DependsOn(MemoDb.TRANSACTION_MANAGER)
class MemoQdslConfig {
    @PersistenceContext(unitName = MemoDb.ENTITY_MANAGER_FACTORY)
    lateinit var memoEntityManager: EntityManager

    @Bean
    fun memoJpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(memoEntityManager)
    }
}
