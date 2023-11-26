plugins {
    conventions
}

dependencies {
    implementation(project(":service:memo:core"))
    implementation(project(":libs:core-kernel"))

    implementation("org.apache.lucene:lucene-core:9.8.0")
    implementation("org.apache.lucene:lucene-queryparser:9.8.0")
    implementation("org.apache.lucene:lucene-analyzers-nori:8.11.2")
}