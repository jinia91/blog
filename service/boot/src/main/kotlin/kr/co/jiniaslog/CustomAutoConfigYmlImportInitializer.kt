package kr.co.jiniaslog

import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.support.ResourcePatternUtils
import java.io.IOException

class CustomAutoConfigYmlImportInitializer : ApplicationContextInitializer<ConfigurableApplicationContext?> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val environment = applicationContext.environment
        val resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(applicationContext)
        val activeProfiles = environment.activeProfiles.toList()

        try {
            val resources = resourcePatternResolver.getResources("classpath*:*application*.yml")
            val loader = YamlPropertySourceLoader()
            for (resource in resources) {
                if (resource.filename != null) {
                    if (resource.filename!!.endsWith("application.yml")) {
                        loader.load(resource.filename, resource)?.forEach {
                            environment.propertySources.addLast(it)
                        }
                    }
                    else if (activeProfiles.any { profile -> resource.filename!!.endsWith("application-$profile.yml") }) {
                        loader.load(resource.filename, resource)?.forEach {
                            environment.propertySources.addLast(it)
                        }
                    }
                }
            }

        } catch (e: IOException) {
            throw IllegalStateException("Failed to load YAML files", e)
        }
    }
}