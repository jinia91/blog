package kr.co.jiniaslog.ai.adapter.outbound.mysql.config

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import kr.co.jiniaslog.shared.adapter.out.rdb.JpaAutoDdlProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["kr.co.jiniaslog.ai.adapter.outbound.mysql"],
    entityManagerFactoryRef = "aiEntityManagerFactory",
    transactionManagerRef = "aiTransactionManager"
)
class AiDatasourceConfig(private val jpaDdlAutoProperty: JpaAutoDdlProperty) {

    @Bean
    @ConfigurationProperties("spring.datasource.ai")
    fun aiDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    fun aiDataSource(): DataSource {
        return aiDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean
    fun aiEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("aiDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("kr.co.jiniaslog.ai.domain.chat")
            .persistenceUnit("ai")
            .properties(mapOf(jpaDdlAutoProperty.key to jpaDdlAutoProperty.ddlAuto))
            .build()
    }

    @Bean
    fun aiTransactionManager(
        @Qualifier("aiEntityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
