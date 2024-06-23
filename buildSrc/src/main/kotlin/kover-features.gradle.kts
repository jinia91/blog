import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import kotlinx.kover.gradle.plugin.dsl.MetricType
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
}

tasks.getByName("koverVerify").dependsOn("koverHtmlReport", "koverXmlReport")

koverReport {
    defaults {
        xml {
            title.set("${project.name} test coverage")
        }
    }
    verify {
        rule(name = "Branch coverage") {
            entity = GroupingEntityType.APPLICATION
            isEnabled = true

            bound {
                metric = MetricType.INSTRUCTION
                minValue = 70
            }

            bound {
                metric = MetricType.BRANCH
                minValue = 70
            }
        }
    }
}
