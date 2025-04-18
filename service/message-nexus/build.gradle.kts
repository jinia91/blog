plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        enabled = true
        archiveFileName.set("${project.parent?.parent?.name}-${project.parent?.name}-${project.name}.jar")
    }
}

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.MessagingHandlerGenerator.path))
    ksp(project(Modules.Libs.MessagingHandlerGenerator.path))
}
