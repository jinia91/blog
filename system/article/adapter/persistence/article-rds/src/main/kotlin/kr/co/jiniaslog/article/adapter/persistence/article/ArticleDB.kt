package kr.co.jiniaslog.article.adapter.persistence.article

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

object ArticleDB {
    const val BASE_PACKAGE = "kr.co.jiniaslog.article.adapter.persistence.article"
    const val DATASOURCE_PREFIX = "spring.datasource.article"

    const val ENTITY_MANAGER_FACTORY = "articleEntityManagerFactory"
    const val PERSISTENT_UNIT = "articleEntityManager"
    const val TRANSACTION_MANAGER = "articleTransactionManager"
}

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = ArticleDB.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = ArticleDB.TRANSACTION_MANAGER,
    basePackages = [ArticleDB.BASE_PACKAGE],
)
class ArticleDatasourceConfig {
    @Bean
    @Primary
    @ConfigurationProperties(prefix = ArticleDB.DATASOURCE_PREFIX)
    fun articleDatasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    @Primary
    fun articleEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(articleDatasource())
            .packages(ArticleDB.BASE_PACKAGE)
            .build()
    }

    @Bean
    @Primary
    fun articleTransactionManager(
        @Qualifier(ArticleDB.ENTITY_MANAGER_FACTORY) entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        transactionManager.persistenceUnitName = ArticleDB.PERSISTENT_UNIT

        return transactionManager
    }
}
