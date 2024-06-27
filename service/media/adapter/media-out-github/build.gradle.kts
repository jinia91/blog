plugins {
    springBootConventions
}

dependencies {
    implementation(project(Modules.Service.Media.Domain.path))
    implementation(project(Modules.Service.Media.Application.path))
    implementation("org.kohsuke:github-api:1.318")
}
