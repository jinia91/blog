plugins {
    conventions
}

tasks.jar {
    enabled = false
}

subprojects {// system modules
    if (project.subprojects.isNotEmpty()) return@subprojects // build only leaf project
    apply(plugin = "conventions")
    val sharedCoreKernel = ":service:libs:core_kernel"
    if (project.path != sharedCoreKernel) {
        dependencies {
            implementation(project(sharedCoreKernel))
        }
    }
}