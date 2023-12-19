
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation(project(":service:memo:memo-core"))
    implementation(project(":libs:core-kernel"))
}