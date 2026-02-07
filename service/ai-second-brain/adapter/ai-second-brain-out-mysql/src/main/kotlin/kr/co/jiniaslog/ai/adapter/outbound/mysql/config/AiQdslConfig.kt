package kr.co.jiniaslog.ai.adapter.outbound.mysql.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AiQdslConfig {
    @PersistenceContext(unitName = "ai")
    lateinit var aiEntityManager: EntityManager

    @Bean
    fun aiJpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(aiEntityManager)
    }
}
