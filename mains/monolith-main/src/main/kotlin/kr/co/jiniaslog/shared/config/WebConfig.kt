package kr.co.jiniaslog.shared.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl.maxAge
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    @Value("\${host.domain}")
    private lateinit var domain: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowCredentials(true)
            .allowedOrigins("http://$domain:3000", "https://$domain", "https://www$domain")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTION")
            .maxAge(3600)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthUserIdArgumentResolver())
    }
}
