# Jinia's Log V2

- [과거 버전의 리드미를 보시려면 클릭](./README_V1.md)

https://www.jiniaslog.co.kr/

# 목차

# 기술 스택

## 인프라 / DevOps
[gitOps Repository](https://github.com/jinia91/blog-gitops)
- 라즈베리파이 4B 4대
  - 4core
  - 8GB Ram
  - os: Ubuntu 22.04.4 LTS
  - sdcard
- 쿠버네티스 클러스터 (k3s)
- ArgoCd
- GitHub Actions

## Observability
- OpenTelemetry
- Prometheus
- Grafana
- Loki
- tempo

## Backend
- Spring Boot 3.3.0
- kotlin 1.9.24
- jpa
- querydsl
- mybatis
- swagger
- gradle

### data processing
- kafka
- kafka connect
  - debezium
  - elasticsearch sink
- ksqlDB
- kafka web ui

### Database
- mysql
- redis sentinel
- elasticsearch

### test / static analysis
- testcontainers
- kotest assertion
- kover
- junit
- rest-assured
- detekt

## Frontend
[Frontend Repository](https://github.com/jinia91/blog-front)

- react 18
- next 14.0.3
- tailwind 3.3.0
- typescript
- jotai
- vitest
- eslint

# 아키텍처

## 인프라

![라즈베리파이 v1](https://github.com/jinia91/blogBackUp/blob/main/img/370345936986144?raw=true)


![인프라 v1](https://github.com/jinia91/blogBackUp/blob/main/img/370720345673760?raw=true)

### As - is

- 라즈베리파이 4대에 각각 sd 카드 장착
- 우분투 위에 K3s를 바이너리로 설치
- 공유기를 통해 사설IP를 할당받고 해당 ip가 노드의 ip가 되는 방식(klipper lb의 동작방식)
- 공유기 앞단에 ddns로 공인 ip 추상화
- 사설ip 하나를 포트포워딩 하는방식

### To - Be

#### 기존 방식의 문제점

-  sd 카드의실질적인 수명 문제
- 상태를 가진 파드의 경우 쿠베임에도 물리적 노드에 종속되는 문제
- 공유기의 내부 트래픽 통제 한계로 확장성 제한
- 포트포워딩되는 ip의 spof 문제


#### to-be  예상 아키텍처
(현재 작업중입니다)

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377460700196896?raw=true)

- diskless pxe 부팅 및 nas로 스토리지를 추상화
- nas 2대간 액티브 스탠바이로 2중화 ha 구성
- metallb 로 lb 교체하여 가상 ip에 포트포워딩, 물리적 SPOF 해소
- 스위치를 두어 내부 트래픽 정체 문제 해소


## 시스템


### As-is
![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377463519752224?raw=true)

- 물리 노드 위에 k3s로 제어되는 클라우드 네이티브 시스템 구축
- 아르고CD와 LGTM 스택으로 gitOps 배포와 옵저버빌리티 시스템 구축
- k3s, kubectl, openlense로 컨테이너오케스트레이션 및 컨트롤

- 주요 애플리케이션 고가용성 구성(HA)

### To-be

- 옵저버빌리티 시스템과 카프카 연동으로 배압 조절
- gitOps 고도화로 모든 컴포넌트의 형상 제어 및 관리

## 애플리케이션

### As-is

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377465355083808?raw=true)

- 바운디드 컨텍스트 별로 모듈화되며 각각의 컨텍스트는 다시 별도의 어댑터와 헥사고날 코어로 모듈화
- 모놀리스 애플리케이션 실행 세팅을 가진 Main 모듈에 각각의 바운디드 컨텍스트 모듈을 끼워넣어 전체 애플리케이션이 구동되는 개념


### To-be

각각 바운디드컨텍스트별 메인을 정의하여 독립적인 애플리케이션으로 분화, msa 로 발전해나가는 과정을 모노레포지토리 내에서 형상관리 예정


# 핵심 컨셉

## 실제 물리 머신 위에 클라우드 네이티브한 인프라 구축 및 운영 경험


![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377787901890592?raw=true)


![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377788209242144?raw=true)


라즈베리파이를 물리노드로 삼아 k3s를 바이너리 배포하여 클라우드 네이티브한 인프라환경을 구축하고, 다양한 애플리케이션들을 리소스세트로 관리하고 배포하여 운영하고 있습니다.

기본적인 k8s 클러스터 운용 경험은 물론, 고가용성과 리소스 사용량간의 트레이드오프, 이를 위한 아키텍처 개선등을 꾸준히 진행하고 있습니다.

### 추가 아티클
- [라즈베리파이에 쿠버네티스 올리기](https://www.jiniaslog.co.kr/blog/370338127683616)


## CI CD 파이프라인 구축 및 고도화

### 정적분석

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377787653210144?raw=true)


잦은 통합시에도 항상 수행해야할 컨벤션체크, 코드스멜감지, 코드 품질 유지를 위해 kolint, detekt를 git hook을 활용해 로컬과 ci 파이프라인상에서 수행하도록 구성했습니다.

### 테스트코드와 커버리지


![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377787094728736?raw=true)


잦은 통합시 의미적 충돌을 방지하고 수정시 회귀적 오류를 막기위해 단위/통합수준의 테스트코드 작성과 이를 강제하기위해 kover를 사용해 커버리지 체크를 파이프라인에 추가하였습니다. 이를 통해 메인브랜치가 isntruction기준 커버리지 최소 80% 이상을 항상 유지하도록 하였습니다.

### cd 파이프라인

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377791659249696?raw=true)

배포시점에는 깃헙액션을 통해 타겟 브랜치 형상을 이미지로 빌드하게되고 해당 이미지 빌드가 완료되면 태그를 깃옵스 레포지토리에 커밋합니다. argoCd는 gitOps 레포지토리의 인프라들과 애플리케이션 모든 형상들을 감지하여 형상 변경시 자동 배포를 수행합니다.

이때 사용되는 민감정보들과 설정정보들은 k3s secret 과 configmap에서 따로 관리 됩니다.

## 옵저버빌리티 시스템으로 장애에 안정적인 시스템 만들기

클라우드네이티브한 인프라와 시스템 구축을 위해 모든 리소스에대한 observability 구축과 적절한 모니터링/알람이 필요합니다.

이를 위해 LGTM(Loki, grafana, tempo, mimir대신 prometheus) 를 채택하고, 다양한 메트릭, 로그, 트레이싱간 프로토콜 일치를 위해 Opentelemetry로  통일시켰습니다.

- 라즈베리파이 인프라 모니터링
  ![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377793381031968?raw=true)


- 쿠버네티스 노드 모니터링
  ![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377793544888352?raw=true)

- 파드별 모니터링
  ![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377793726173216?raw=true)

- 메인애플리케이션 모니터링
  ![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377793843290144?raw=true)

이를 통해 언제든지 이상감지를 확인하고 모니터링할수 있는 시스템을 구축하였고 임계점기준 알람을 통해 시스템 장애에 즉각 대응할 수 있도록 해두었습니다.

템포는 현재 사용하고있지않지만 추후 msa의 확장을 고려해 설치해두었으며, 메트릭, 로그의 내부 트래픽 제어와 배압조절을 위해 카프카와 연동을 계획하고있습니다.

## 모듈화된 애플리케이션으로 MSA로의 진화 확장성 확보 및 실현

DDD 관점에서 비즈니스 영역을 각각의 바운디드 컨텍스트로 정의하고 분리하여 초기부터 모듈화된 아키텍처를 통해 강한 디커플링을 시도하였습니다.

이를 통해 각 바운디드 컨텍스트는 메인 모듈만 따로 마련되면 언제든지 분리되어 독립적인 서버로 분리될수 있게 설계했으며

데이터 베이스들 역시 다양한 데이터소스를 사용하고 각 바운디드 컨텍스트별로 독립적인 db스키마를 채택하여 물리적으로는 같은db더라도 별도의 독립적인 db로 분리될수 있도록 설계했습니다.

추후 컨텍스트들을 하나씩 독립시키며 msa구조로의 전환을 할 예정입니다.

#### 이를 위한 gradle module 선언

```
rootProject.name = "Jinia's Log"

include(
    /**
     * ################
     * ## Jinia's Log##
     * ################
     */

    // framework 의존성을 가지고 순수하게 애플리케이션을 실행시키는 main module
    "mains:monolith-main",

    // 서비스 공용 라이브러리
    "libs:core-kernel",
    "libs:messaging-handler-generator",
    "libs:rdb-kernel",
    "libs:rest-kernel",
    "libs:websocket-kernel",
    "libs:messaging-kernel",
    "libs:snowflake-id-generator",

    // blog
    "service:blog:blog-core",
    "service:blog:adapter:blog-in-http",
    "service:blog:adapter:blog-in-acl",
    "service:blog:adapter:blog-in-batch",
    "service:blog:adapter:blog-in-message",
    "service:blog:adapter:blog-in-websocket",
    "service:blog:adapter:blog-out-rdb",
    "service:blog:adapter:blog-out-user",
    "service:blog:adapter:blog-out-memo",
    "service:blog:adapter:blog-out-mysql",
    "service:blog:adapter:blog-out-es",

    // memo
    "service:memo:memo-core",
    "service:memo:adapter:memo-in-http",
    "service:memo:adapter:memo-in-acl",
    "service:memo:adapter:memo-in-websocket",
    "service:memo:adapter:memo-in-message",
    "service:memo:adapter:memo-in-batch",
    "service:memo:adapter:memo-out-mysql",
    "service:memo:adapter:memo-out-user",

    // media
    "service:media:media-domain",
    "service:media:media-application",
    "service:media:adapter:media-in-http",
    "service:media:adapter:media-out-github",

    // user-auth
    "service:user-auth:user-auth-domain",
    "service:user-auth:user-auth-application",
    "service:user-auth:adapter:user-auth-in-http",
    "service:user-auth:adapter:user-auth-in-acl",
    "service:user-auth:adapter:user-auth-out-google",
    "service:user-auth:adapter:user-auth-out-mysql",
    "service:user-auth:adapter:user-auth-out-cache",

    // comment
    "service:comment",
    "service:comment:adapter:comment-in-http",
    "service:comment:comment-core",
    "service:comment:adapter:comment-out-mysql",
    "service:comment:adapter:comment-out-blog",
    "service:comment:adapter:comment-out-user",

    // admin
    "service:admin:admin-core",
    "service:admin:adapter:admin-in-http",

    "service:seo",
    "service:message-nexus",
)
```

- 모듈화된 구조
  ![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377466463211552?raw=true)


## CQRS 등 데이터 파이프라인, 시스템을 통한 다양한 아키텍처 패턴 구현

![uploaded image](https://github.com/jinia91/blogBackUp/blob/main/img/377796284940320?raw=true)


강한 스키마와 트랜잭션 제어를 통해 데이터 일관성, 무결성을 보장하는 rdb를 cud 오퍼레이션 DB로 삼고 카프카와 데비지움 커넥터를 사용해 엘라스틱서치로 데이터를 동기화시키고 역인덱싱하여,

검색과 읽기에 최적화된 read only 파이프라인을 별도로 구축하여 CQRS를 구현하였습니다.

추후 msa 분리시 시스템 자체도 read only시스템을 별도로 분리할 계획을 하고있습니다.
# articles
- [라즈베리파이에 쿠버네티스 올리기](https://www.jiniaslog.co.kr/blog/370338127683616)
- 인프라 고도화
- CI/CD
- Observability
- 모듈화된 아키텍처
- msa