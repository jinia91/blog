# Jinia's Log V2

- [과거 버전의 리드미를 보려하시면 클릭](./README_V1.md)

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

작성중

