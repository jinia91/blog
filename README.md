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


### To-be

각각 바운디드컨텍스트별 메인을 정의하여 독립적인 애플리케이션으로 분화, msa 로 발전해나가는 과정을 모노레포지토리 내에서 형상관리 예정


# 핵심 컨셉

## 실제 물리 머신 위에 클라우드 네이티브한 인프라 구축 및 운영 경험

## CI CD 파이프라인 구축 및 고도화

## 옵저버빌리티 시스템으로 장애에 안정적인 시스템 만들기

## 모듈화된 애플리케이션으로 MSA로의 진화 확장성 확보 및 실현

## CQRS 등 데이터 파이프라인, 시스템을 통한 다양한 아키텍처 패턴 구현

# articles
- [라즈베리파이에 쿠버네티스 올리기](https://www.jiniaslog.co.kr/blog/370338127683616)