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

# 핵심 키워드

- 인프라부터 프론트까지 / 기획부터 운영까지 웹 애플리케이션 전 범위와 생애 주기 전체를 아우르는 경험
- 인프라단 HA 고려
- k8s 클러스터 운영 경험
- 스프링프레임워크와 물리적으로 모듈화된 아키텍처에서 MSA로의 진화과정
- 장기 생산성을 고려한 CI / CD 파이프라인 구축

# articles
- [라즈베리파이에 쿠버네티스 올리기](https://www.jiniaslog.co.kr/blog/370338127683616)
- 인프라 고도화
- CI/CD
- Observability
- 모듈화된 아키텍처
- msa

