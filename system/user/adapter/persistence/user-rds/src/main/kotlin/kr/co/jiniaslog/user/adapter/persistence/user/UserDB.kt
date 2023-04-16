package kr.co.jiniaslog.user.adapter.persistence.user

import jakarta.annotation.PostConstruct
import kr.co.jiniaslog.shared.persistence.JpaDdlAutoProperties
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
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
import java.nio.charset.Charset
import javax.sql.DataSource

object UserDB {
    const val BASE_PACKAGE = "kr.co.jiniaslog.user.adapter.persistence.user"
    const val DATASOURCE_PREFIX = "spring.datasource.user"
    const val DATASOURCE = "userDataSource"

    const val ENTITY_MANAGER_FACTORY = "userEntityManagerFactory"
    const val PERSISTENT_UNIT = "userEntityManager"
    const val TRANSACTION_MANAGER = "userTransactionManager"
    const val FLYWAY_SCRIPT_LOCATION = "db.migration.user"
    const val FLYWAY_SCRIPT_CHARSET = "UTF-8"
}

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = UserDB.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = UserDB.TRANSACTION_MANAGER,
    basePackages = [UserDB.BASE_PACKAGE],
)
class UserDatasourceConfig(
    private val property: JpaDdlAutoProperties,
) {
    @Bean(name = [UserDB.DATASOURCE])
    @ConfigurationProperties(prefix = UserDB.DATASOURCE_PREFIX)
    fun userDatasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun userEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        val properties: MutableMap<String, Any> = HashMap()
        properties["hibernate.hbm2ddl.auto"] = property.value

        return builder
            .dataSource(userDatasource())
            .packages(UserDB.BASE_PACKAGE)
            .persistenceUnit(UserDB.PERSISTENT_UNIT)
            .properties(properties)
            .build()
    }

    @Bean
    fun userTransactionManager(
        builder: EntityManagerFactoryBuilder,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = userEntityManagerFactory(builder).`object`
        transactionManager.persistenceUnitName = UserDB.PERSISTENT_UNIT

        return transactionManager
    }
}

@Configuration
class UserSchemaVersioning(
    @Qualifier(UserDB.DATASOURCE) private val userDataSource: DataSource,
) {
    @PostConstruct
    fun migrateBatchDataSource() {
        Flyway.configure()
            .dataSource(userDataSource)
            .locations(UserDB.FLYWAY_SCRIPT_LOCATION)
            .baselineOnMigrate(true)
            .encoding(Charset.forName(UserDB.FLYWAY_SCRIPT_CHARSET))
            .load()
            .migrate()
    }

    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy { flyway -> flyway.migrate() }
    }
}
