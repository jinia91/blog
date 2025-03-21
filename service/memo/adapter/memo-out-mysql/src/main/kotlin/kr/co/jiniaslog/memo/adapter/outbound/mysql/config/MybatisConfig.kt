package kr.co.jiniaslog.memo.adapter.outbound.mysql.config

import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class MybatisConfig {
    @Bean
    fun memoSqlSessionFactory(
        @Qualifier(MemoDb.DATASOURCE) dataSource: DataSource
    ): SqlSessionFactory {
        val sessionFactoryBean = SqlSessionFactoryBean()
        sessionFactoryBean.setDataSource(dataSource)
        return sessionFactoryBean.`object`!!
    }

    @Bean
    fun memoSqlSessionTemplate(
        @Qualifier("memoSqlSessionFactory") sqlSessionFactory: SqlSessionFactory
    ): SqlSessionTemplate {
        return SqlSessionTemplate(sqlSessionFactory)
    }
}
