package kr.co.jiniaslog.blog.adapter.outbound.mysql.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BlogQdslConfig {
    @PersistenceContext(name = "blogEntityManager")
    lateinit var blogEntityManager: EntityManager

    @Bean
    fun blogJpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(blogEntityManager)
    }
}
