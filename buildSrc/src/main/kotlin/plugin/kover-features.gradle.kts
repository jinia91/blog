import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import kotlinx.kover.gradle.plugin.dsl.MetricType
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
}

tasks.getByName("koverVerify").dependsOn("koverHtmlReport")

koverReport {
    filters {
        excludes {
            // 설정 클래스 제외
            classes("*Config", "*Config\$*", "*Configuration", "*Configuration\$*")
            // Spring 컴포넌트 인터페이스 제외
            classes("*Properties", "*Properties\$*")
            // 이벤트 리스너 제외
            classes("*EventListener", "*EventListener\$*")
            // 외부 의존성 어댑터 제외 (실제 테스트는 통합 테스트로)
            classes(
                "*ChromaEmbeddingStoreAdapter*",
                "*LlmServiceImpl*",
                "*MemoQueryServiceAdapter*",
                "*MemoCommandServiceAdapter*",
                "*MemoQueryClientAdapter*",
            )
            // DTO 및 데이터 클래스 (equals, hashCode, toString 등 자동 생성)
            classes("*Dto", "*Dto\$*", "*Request", "*Request\$*", "*Response", "*Response\$*")
            // Payload 및 ViewModel 클래스
            classes("*Payload", "*Payload\$*", "*ViewModel", "*ViewModel\$*")
            // WebSocket 핸들러 (통합 테스트로만 가능)
            classes("*WebSocketHandler*", "*WsHandler*")
            // SEO 관련 (정적 콘텐츠 생성)
            packages("kr.co.jiniaslog.seo.*")
            // Batch 작업 (스케줄러 통합 테스트로만)
            packages("*.batch")
            // 외부 서비스 어댑터 (Google, GitHub 등)
            classes("*GoogleAuthAdapter*", "*ImageUploaderGithub*")
            // WebSocket 패키지 (실시간 통신, 통합 테스트로)
            packages("*.websocket")
            // ACL 패키지 (Anti-corruption layer, 통합 테스트로)
            packages("*.acl")
            // 공유 인프라 클래스
            classes("*IdGenerator*", "*Cleaner*")
            // Elasticsearch 관련 (외부 의존성)
            packages("*.elasticsearch")
            // 외부 서비스 연동 패키지
            packages("*.github")
            packages("*.google")
            // message 패키지 (이벤트 핸들링)
            packages("*.message")
            // shared 인프라 관련
            packages("kr.co.jiniaslog.shared.*")
        }
    }

    verify {
        rule(name = "Branch coverage") {
            entity = GroupingEntityType.APPLICATION
            isEnabled = true

            bound {
                metric = MetricType.INSTRUCTION
                minValue = 80
            }

            bound {
                metric = MetricType.BRANCH
                minValue = 80
            }

            bound {
                metric = MetricType.LINE
                minValue = 80
            }
        }
    }
}
