package kr.co.jiniaslog.memo.adapter.outbound.mysql.config

import jakarta.persistence.EntityManagerFactory
import kr.co.jiniaslog.memo.adapter.outbound.mysql.config.MemoDb.TRANSACTION_MANAGER
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

object MemoDb {
    const val BASE_PACKAGE = "kr.co.jiniaslog.memo"
    const val DATASOURCE_PREFIX = "spring.datasource.memo"
    const val ENTITY_MANAGER_FACTORY = "memoEntityManagerFactory"
    const val DATASOURCE = "memoDatasource"
    const val PERSISTENT_UNIT = "memoEntityManager"
    const val TRANSACTION_MANAGER = "memoTransactionManager"
}

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = MemoDb.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = MemoDb.TRANSACTION_MANAGER,
    basePackages = [MemoDb.BASE_PACKAGE],
)
class MemoDatasourceConfig(private val jpaDdlAutoProperty: JpaAutoDdlProperty) {
    @Bean
    @ConfigurationProperties(prefix = MemoDb.DATASOURCE_PREFIX)
    fun memoDatasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun memoEntityManagerFactory(
        @Qualifier(MemoDb.DATASOURCE) dataSource: DataSource,
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages(MemoDb.BASE_PACKAGE)
            .properties(mapOf(jpaDdlAutoProperty.key to jpaDdlAutoProperty.ddlAuto))
            .build()
    }

    @Bean(name = [TRANSACTION_MANAGER])
    fun memoTransactionManager(
        @Qualifier(MemoDb.ENTITY_MANAGER_FACTORY) entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        transactionManager.persistenceUnitName = MemoDb.PERSISTENT_UNIT
        return transactionManager
    }
}
