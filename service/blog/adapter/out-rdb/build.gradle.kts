plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:blog:core"))
    implementation(project(":libs:core-kernel"))
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    api("com.github.jasync-sql:jasync-mysql:2.1.23")
    implementation("io.asyncer:r2dbc-mysql:1.0.5")
}