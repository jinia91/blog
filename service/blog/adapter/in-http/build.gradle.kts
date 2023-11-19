plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:blog:core"))
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:http-kernel"))
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.4")
}