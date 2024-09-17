plugins {
    id("conventions")
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry:opentelemetry-bom:1.34.1")
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.6.0")
    }
}

dependencies {
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
}
