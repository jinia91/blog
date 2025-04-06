package kr.co.jiniaslog.comment.adapter.outbound.mysql.config

import jakarta.persistence.EntityManagerFactory
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAutoDdlProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

object CommentDb {
    const val BASE_PACKAGE = "kr.co.jiniaslog.comment"
    const val DATASOURCE_PREFIX = "spring.datasource.comment"
    const val ENTITY_MANAGER_FACTORY = "commentEntityManagerFactory"
    const val DATASOURCE = "commentDatasource"
    const val PERSISTENT_UNIT = "commentEntityManager"
    const val TRANSACTION_MANAGER = "commentTransactionManager"
}

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = CommentDb.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = CommentDb.TRANSACTION_MANAGER,
    basePackages = [CommentDb.BASE_PACKAGE],
)
class CommentDatasourceConfig(private val jpaDdlAutoProperty: JpaAutoDdlProperty) {
    @Bean
    @ConfigurationProperties(prefix = CommentDb.DATASOURCE_PREFIX)
    fun commentDatasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun commentEntityManagerFactory(
        @Qualifier(CommentDb.DATASOURCE) dataSource: DataSource,
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages(CommentDb.BASE_PACKAGE)
            .properties(mapOf(jpaDdlAutoProperty.key to jpaDdlAutoProperty.ddlAuto))
            .build()
    }

    @Bean(name = [CommentDb.TRANSACTION_MANAGER])
    fun commentTransactionManager(
        @Qualifier(CommentDb.ENTITY_MANAGER_FACTORY) entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        transactionManager.persistenceUnitName = CommentDb.PERSISTENT_UNIT

        return transactionManager
    }
}
