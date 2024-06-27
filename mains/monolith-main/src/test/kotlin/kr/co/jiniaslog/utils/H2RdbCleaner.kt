package kr.co.jiniaslog.utils

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.stereotype.Component
import java.sql.Connection
import javax.sql.DataSource

@Component
class H2RdbCleaner(private val dataSources: List<DataSource>) : DbCleaner {
    override fun tearDownAll() {
        dataSources.forEach { dataSource ->
            clean(dataSource)
        }
    }

    private fun clean(dataSource: DataSource) {
        val jdbcTemplate = JdbcTemplate(dataSource)
        var connection: Connection? = null
        try {
            connection = DataSourceUtils.getConnection(jdbcTemplate.dataSource!!)

            val tableNames: List<String> =
                jdbcTemplate.query(
                    QUERY_TABLE_NAMES,
                    arrayOf(H2_SCHEMA),
                ) { resultSet, _ ->
                    resultSet.getString(TABLE_NAME)
                }
            tableNames.forEach { tableName ->
                jdbcTemplate.execute(String.format(DELETE_ALL_TABLES, tableName))
            }
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, jdbcTemplate.dataSource!!)
            }
        }
    }

    companion object {
        private const val QUERY_TABLE_NAMES = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?"
        private const val H2_SCHEMA = "PUBLIC"
        private const val TABLE_NAME = "table_name"
        private const val DELETE_ALL_TABLES = "DELETE FROM %s"
    }
}
