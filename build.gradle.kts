import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
}

tasks.withType<Detekt>().configureEach {
    ignoreFailures = false
    autoCorrect = true
    reports {
        html.required.set(true)
    }
    parallel = true
    setSource(file(projectDir))
    exclude(
        "**/build/**",
        "**/generated/**",
        "**/resources/**",
        // fixme https://github.com/detekt/detekt/issues/7005 버그로 제외
        "**/**FragmentsTests",
        "**/test/**",
        "**/testFixtures/**",
    )
}

repositories {
    mavenCentral()
}

val detektVersion = "1.23.5"
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:$detektVersion")
}

tasks.register("importGitPreCommitHook", Copy::class) {
    val preCommitHookFile = file(".git/hooks/pre-commit")
    if (preCommitHookFile.exists()) {
        println("> \"${preCommitHookFile.absolutePath}\" 이 이미 존재합니다.")
        println("> \"${preCommitHookFile.absolutePath}\" 을 파일을 삭제합니다.")
        preCommitHookFile.delete()
    }
    exec {
        println("> pre-commit 심볼릭 링크 생성")
        commandLine = listOf("ln", "-s", file("githooks/pre-commit").absolutePath, ".git/hooks")
    }.assertNormalExitValue()
}

tasks.register("importGitPrePushHook", Copy::class) {
    val prePushHookFile = file(".git/hooks/pre-push")
    if (prePushHookFile.exists()) {
        println("> \"${prePushHookFile.absolutePath}\" 이 이미 존재합니다.")
        println("> \"${prePushHookFile.absolutePath}\" 을 파일을 삭제합니다.")
        prePushHookFile.delete()
    }
    exec {
        println("> pre-commit 심볼릭 링크 생성")
        commandLine = listOf("ln", "-s", file("githooks/pre-push").absolutePath, ".git/hooks")
    }.assertNormalExitValue()
}
