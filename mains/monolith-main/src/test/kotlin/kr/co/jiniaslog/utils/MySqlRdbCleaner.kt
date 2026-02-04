package kr.co.jiniaslog.utils

import com.zaxxer.hikari.HikariDataSource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.jdbc.datasource.DelegatingDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import javax.sql.DataSource

@Component
class MySqlRdbCleaner(private val dataSources: List<DataSource>) : DbCleaner {
    override fun tearDownAll() {
        dataSources
            .filter { !isH2DataSource(it) }
            .forEach { dataSource ->
                clean(dataSource)
            }
    }

    private fun clean(dataSource: DataSource) {
        val jdbcTemplate = JdbcTemplate(dataSource)
        var connection: Connection? = null
        try {
            connection = DataSourceUtils.getConnection(jdbcTemplate.dataSource!!)
            val schema: String = connection.catalog

            val tableNames: List<String> =
                jdbcTemplate.query(
                    NO_LOG + QUERY_TABLE_NAMES,
                    arrayOf(schema),
                ) { resultSet, _ ->
                    resultSet.getString(TABLE_NAME)
                }
            jdbcTemplate.execute(NO_LOG + "SET FOREIGN_KEY_CHECKS=0;")
            tableNames.forEach { tableName ->
                jdbcTemplate.execute(NO_LOG + "TRUNCATE TABLE $tableName")
            }
            jdbcTemplate.execute(NO_LOG + "SET FOREIGN_KEY_CHECKS=1;")
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, jdbcTemplate.dataSource!!)
            }
        }
    }

    private fun isH2DataSource(dataSource: DataSource): Boolean {
        return try {
            // Try to find HikariDataSource in the wrapper chain
            val hikari = findHikariDataSource(dataSource)
            if (hikari != null) {
                val jdbcUrl = hikari.jdbcUrl ?: ""
                return jdbcUrl.startsWith("jdbc:h2:")
            }
            // Fallback: check toString for H2 indicators
            dataSource.toString().contains("h2", ignoreCase = true)
        } catch (e: Exception) {
            // If we can't determine, assume it IS H2 to be safe (skip it)
            true
        }
    }

    private fun findHikariDataSource(dataSource: DataSource): HikariDataSource? {
        var current: Any? = dataSource
        val visited = mutableSetOf<Any>()

        while (current != null && current !in visited) {
            visited.add(current)

            if (current is HikariDataSource) {
                return current
            }

            // Try to get the wrapped/target DataSource using reflection
            current = try {
                when {
                    current is DelegatingDataSource -> current.targetDataSource
                    else -> {
                        // Try common field names for wrapped DataSources
                        val clazz = current::class.java
                        listOf("realDataSource", "targetDataSource", "delegate", "dataSource", "ds")
                            .firstNotNullOfOrNull { fieldName ->
                                try {
                                    val field = clazz.getDeclaredField(fieldName)
                                    field.isAccessible = true
                                    field.get(current) as? DataSource
                                } catch (e: Exception) {
                                    null
                                }
                            }
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    companion object {
        private const val NO_LOG = "-- NO_LOG \n"
        private const val QUERY_TABLE_NAMES = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?"
        private const val TABLE_NAME = "table_name"
    }
}
