import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

val Project.libs get() = the<LibrariesForLibs>()

inline val PluginDependenciesSpec.convention: PluginDependencySpec
    get() = id("convention")
