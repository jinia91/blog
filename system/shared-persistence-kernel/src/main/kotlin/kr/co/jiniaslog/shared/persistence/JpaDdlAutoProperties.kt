package kr.co.jiniaslog.shared.persistence

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "jpa.ddl.auto")
data class JpaDdlAutoProperties @ConstructorBinding constructor(
    val value: String,
)
