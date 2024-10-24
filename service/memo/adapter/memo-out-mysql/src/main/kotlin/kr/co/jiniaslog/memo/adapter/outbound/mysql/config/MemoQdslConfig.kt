package kr.co.jiniaslog.memo.adapter.outbound.mysql.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MemoQdslConfig {
    @Autowired
    @Qualifier(MemoDb.ENTITY_MANAGER_FACTORY)
    lateinit var memoEntityManagerFactory: EntityManagerFactory

    @Bean
    fun memoJpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(memoEntityManagerFactory.createEntityManager())
    }
}
