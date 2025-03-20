import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

val PluginDependenciesSpec.conventions: PluginDependencySpec
    get() = id("conventions")

val PluginDependenciesSpec.springBootConventions: PluginDependencySpec
    get() = id("spring-boot-conventions")

val PluginDependenciesSpec.koverReport: PluginDependencySpec
    get() = id("kover-features")

val PluginDependenciesSpec.observability: PluginDependencySpec
    get() = id("observability")
