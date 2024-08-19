package kr.co.jiniaslog.utils

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.stereotype.Component
import java.sql.Connection
import javax.sql.DataSource

@Component
class MySqlRdbCleaner(private val dataSources: List<DataSource>) : DbCleaner {
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

    companion object {
        private const val NO_LOG = "-- NO_LOG \n"
        private const val QUERY_TABLE_NAMES = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?"
        private const val TABLE_NAME = "table_name"
    }
}
