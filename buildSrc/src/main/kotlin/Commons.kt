import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

val Project.libs get() = the<LibrariesForLibs>()

val PluginDependenciesSpec.conventions: PluginDependencySpec
    get() = id("conventions")

val PluginDependenciesSpec.springBootConventions: PluginDependencySpec
    get() = id("spring-boot-conventions")