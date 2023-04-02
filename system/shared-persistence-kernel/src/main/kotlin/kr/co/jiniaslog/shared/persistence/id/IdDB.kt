package kr.co.jiniaslog.shared.persistence.id

import jakarta.persistence.EntityManagerFactory
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

object IdDB {
    const val BASE_PACKAGE = "kr.co.jiniaslog.shared.persistence.id"
    const val DATASOURCE_PREFIX = "spring.datasource.id"

    const val ENTITY_MANAGER_FACTORY = "idEntityManagerFactory"
    const val PERSISTENT_UNIT = "idEntityManager"
    const val TRANSACTION_MANAGER = "idTransactionManager"
}

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = IdDB.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = IdDB.TRANSACTION_MANAGER,
    basePackages = [IdDB.BASE_PACKAGE],
)
class IdDatasourceConfig {
    @Bean
    @ConfigurationProperties(prefix = IdDB.DATASOURCE_PREFIX)
    fun idDatasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun idEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(idDatasource())
            .packages(IdDB.BASE_PACKAGE)
            .build()
    }

    @Bean
    fun idTransactionManager(
        @Qualifier(IdDB.ENTITY_MANAGER_FACTORY) entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        transactionManager.persistenceUnitName = IdDB.PERSISTENT_UNIT

        return transactionManager
    }
}
