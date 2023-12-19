package kr.co.jiniaslog.media.outbound.github

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.beans.ConstructorProperties

@Configuration
@EnableConfigurationProperties(value = [ConstructorProperties::class])
class GithubConfig
