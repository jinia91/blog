plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:core-kernel"))
    //swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
}