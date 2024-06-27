package kr.co.jiniaslog.shared.adapter.out.rdb

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

/**
 * 멀티 데이터소스의 경우 각 데이터 소스로 jpa ddlAuto 설정이 바인딩되지 않기 때문에 별도로 세팅을 해주어야한다
 */
@ConfigurationProperties("spring.jpa.hibernate")
data class JpaAutoDdlProperty @ConstructorBinding constructor(val ddlAuto: String) {
    val key: String = DDL_SETTING_KEY

    companion object {
        private const val DDL_SETTING_KEY = "hibernate.hbm2ddl.auto"
    }
}
