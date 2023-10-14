plugins {
    conventions
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    enabled = false
}

subprojects {// system modules
    if (project.subprojects.isNotEmpty()) return@subprojects // build only leaf project
    apply(plugin = "conventions")

    val sharedCoreKernel = ":system:shared:core_kernel"
    if (project.path != sharedCoreKernel) {
        dependencies {
            implementation(project(sharedCoreKernel))
        }
    }

}