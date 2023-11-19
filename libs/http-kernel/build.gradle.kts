plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:core-kernel"))
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.4")
}