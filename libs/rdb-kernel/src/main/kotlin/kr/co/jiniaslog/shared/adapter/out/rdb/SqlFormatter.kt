package kr.co.jiniaslog.shared.adapter.out.rdb

import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.Date

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
        targetSql = formatSql(category, targetSql)
        val currentDate = Date()
        val format1 = SimpleDateFormat("yy.MM.dd HH:mm:ss")

//        return now + "|" + elapsed + "ms|" + category + "|connection " + connectionId + "|" + P6Util.singleLine(prepared) + sql;
        return format1.format(currentDate) + " | " + "OperationTime : " + elapsed + "ms" + targetSql
    }

    fun formatSql(
        category: String,
        sql: String?,
    ): String? {
        if (sql == null || sql!!.trim { it <= ' ' } == "") return sql
        return sql
    }
}
