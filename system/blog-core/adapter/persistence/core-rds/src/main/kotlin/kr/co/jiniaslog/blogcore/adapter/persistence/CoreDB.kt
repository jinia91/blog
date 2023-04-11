package kr.co.jiniaslog.blogcore.adapter.persistence

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

object CoreDB {
    const val BASE_PACKAGE = "kr.co.jiniaslog.blogcore.adapter.persistence"
    const val DATASOURCE_PREFIX = "spring.datasource.blogcore"
    const val DATASOURCE = "blogCoreDataSource"

    const val ENTITY_MANAGER_FACTORY = "blogCoreEntityManagerFactory"
    const val PERSISTENT_UNIT = "blogCoreEntityManager"
    const val TRANSACTION_MANAGER = "blogCoreTransactionManager"
}

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = CoreDB.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = CoreDB.TRANSACTION_MANAGER,
    basePackages = [CoreDB.BASE_PACKAGE],
)
class BlogCoreDatasourceConfig(
    private val property: JpaDdlAutoProperties,
) {
    @Bean(name = [CoreDB.DATASOURCE])
    @Primary
    @ConfigurationProperties(prefix = CoreDB.DATASOURCE_PREFIX)
    @FlywayDataSource
    fun blogCoreDatasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    @Primary
    fun blogCoreEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        val properties: MutableMap<String, Any> = HashMap()
        properties["hibernate.hbm2ddl.auto"] = property.value

        return builder
            .dataSource(blogCoreDatasource())
            .packages(CoreDB.BASE_PACKAGE)
            .properties(properties)
            .build()
    }

    @Bean
    @Primary
    fun blogCoreTransactionManager(
        builder: EntityManagerFactoryBuilder,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = blogCoreEntityManagerFactory(builder).`object`
        transactionManager.persistenceUnitName = CoreDB.PERSISTENT_UNIT

        return transactionManager
    }
}
