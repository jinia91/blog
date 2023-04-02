package kr.co.jiniaslog.shared.persistence.id

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class IdDB {
    @Value("\${spring.datasource.id.jdbc-url}")
    private val jdbcUrl: String? = null

    @Value("\${spring.datasource.id.username}")
    private val username: String? = null

    @Value("\${spring.datasource.id.password}")
    private val password: String? = null

    @Value("\${spring.datasource.id.driver-class-name}")
    private val driverClassName: String? = null

    @Bean
    fun idDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(driverClassName!!)
        dataSource.url = jdbcUrl
        dataSource.username = username
        dataSource.password = password
        return dataSource
    }

    @Bean
    fun idJdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(idDataSource())
    }

    @Bean
    fun idTransactionManager(
        @Qualifier("idDataSource") idDataSource: DataSource,
    ): PlatformTransactionManager {
        return DataSourceTransactionManager(idDataSource)
    }
}
