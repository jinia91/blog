plugins {
    conventions
}

tasks.jar {
    enabled = false
}

tasks.jacocoTestReport {
    enabled = false
}

tasks.jacocoTestCoverageVerification {
    enabled = false
}


subprojects {// system modules
    if (project.subprojects.isNotEmpty()) return@subprojects // build only leaf project
    apply(plugin = "conventions")
}