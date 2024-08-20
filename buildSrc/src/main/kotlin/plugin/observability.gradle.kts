plugins {
    id("conventions")
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry:opentelemetry-bom:1.34.1")
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom-alpha:2.0.0-alpha")
    }
}

dependencies {
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.micrometer:micrometer-registry-otlp")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.7.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc")
}
