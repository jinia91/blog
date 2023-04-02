package kr.co.jiniaslog.article.adapter.persistence.article

import kr.co.jiniaslog.shared.persistence.JpaDdlAutoProperties
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
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
    const val DATASOURCE = "articleDataSource"

    const val ENTITY_MANAGER_FACTORY = "articleEntityManagerFactory"
    const val PERSISTENT_UNIT = "articleEntityManager"
    const val TRANSACTION_MANAGER = "articleTransactionManager"
    const val FLYWAY_SCRIPT_LOCATION = "db/migration/article"
    const val FLYWAY_SCRIPT_CHARSET = "UTF-8"
}

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = ArticleDB.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = ArticleDB.TRANSACTION_MANAGER,
    basePackages = [ArticleDB.BASE_PACKAGE],
)
class ArticleDatasourceConfig(
    private val property: JpaDdlAutoProperties,
) {
    @Bean(name = [ArticleDB.DATASOURCE])
    @Primary
    @ConfigurationProperties(prefix = ArticleDB.DATASOURCE_PREFIX)
    @FlywayDataSource
    fun articleDatasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    @Primary
    fun articleEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        val properties: MutableMap<String, Any> = HashMap()
        properties["hibernate.hbm2ddl.auto"] = property.value

        return builder
            .dataSource(articleDatasource())
            .packages(ArticleDB.BASE_PACKAGE)
            .properties(properties)
            .build()
    }

    @Bean
    @Primary
    fun articleTransactionManager(
        builder: EntityManagerFactoryBuilder,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = articleEntityManagerFactory(builder).`object`
        transactionManager.persistenceUnitName = ArticleDB.PERSISTENT_UNIT

        return transactionManager
    }
}
