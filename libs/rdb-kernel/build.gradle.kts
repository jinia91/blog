plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:core-kernel"))
    api("org.springframework.boot:spring-boot-starter-data-r2dbc")
    api("com.github.jasync-sql:jasync-mysql:2.1.23")
    implementation("io.asyncer:r2dbc-mysql:1.0.5")
    implementation("org.flywaydb:flyway-mysql:9.16.1")
//    implementation("org.flywaydb:flyway-core:9.16.1")
    implementation("mysql:mysql-connector-java:8.0.26")
}