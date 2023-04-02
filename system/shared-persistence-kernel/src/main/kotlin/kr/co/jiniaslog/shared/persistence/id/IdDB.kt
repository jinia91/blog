package kr.co.jiniaslog.shared.persistence.id

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

object IdDB {
    const val BASE_PACKAGE = "kr.co.jiniaslog.shared.persistence.id"
    const val DATASOURCE_PREFIX = "spring.datasource.id"
    const val DATASOURCE = "idDataSource"

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
class IdDataSourceConfig {

    @Bean(name = [IdDB.DATASOURCE])
    @ConfigurationProperties(prefix = IdDB.DATASOURCE_PREFIX)
    fun idDataSource(): DataSource = DataSourceBuilder.create().build()

    @Bean(name = [IdDB.ENTITY_MANAGER_FACTORY])
    fun idEntityManagerFactory(
        @Qualifier(IdDB.DATASOURCE) dataSource: DataSource,
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean = builder.dataSource(dataSource)
        .packages(IdDB.BASE_PACKAGE)
        .persistenceUnit(IdDB.PERSISTENT_UNIT)
        .build()

    @Bean(name = [IdDB.TRANSACTION_MANAGER])
    fun idTransactionManager(
        @Qualifier(IdDB.ENTITY_MANAGER_FACTORY) emf: EntityManagerFactory,
    ) = JpaTransactionManager().apply {
        this.entityManagerFactory = emf
    }

    @Bean
    fun idJdbcTemplate(@Qualifier(IdDB.DATASOURCE) dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}
