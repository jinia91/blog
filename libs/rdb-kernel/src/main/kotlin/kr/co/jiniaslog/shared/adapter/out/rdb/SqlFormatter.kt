package kr.co.jiniaslog.shared.adapter.out.rdb

import com.p6spy.engine.logging.Category
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle
import org.springframework.stereotype.Component
import java.util.Locale

@Component
internal class SqlFormatter : MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String,
        elapsed: Long,
        category: String,
        prepared: String,
        sql: String,
        url: String,
    ): String {
        var targetSql: String? = sql
        if (sql.contains("-- NO_LOG")) return ""
        targetSql = formatSql(category, targetSql)

        return "| OperationTime : ${elapsed}ms | $category | connection $connectionId $targetSql"
    }

    private fun formatSql(
        category: String,
        sql: String?,
    ): String? {
        var targetSql = sql
        if (targetSql == null || targetSql!!.trim { it <= ' ' } == "") return targetSql

        if (Category.STATEMENT.name.equals(category)) {
            val tmpSql = targetSql!!.trim { it <= ' ' }.lowercase(Locale.ROOT)
            targetSql =
                if (tmpSql.startsWith("create") || tmpSql.startsWith("alter") || tmpSql.startsWith("drop")) {
                    FormatStyle.DDL.formatter.format(targetSql)
                } else {
                    FormatStyle.BASIC.formatter.format(targetSql)
                }
            targetSql = "|$targetSql"
        }
        return targetSql
    }
}
