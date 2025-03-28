#!/bin/bash

set +e
export $DEFAULT_IP=$(ipconfig getifaddr en0)
echo "프로젝트 클린 및 빌드 중..."
cur_dir=$(cd $(dirname -- "$0") && pwd)
set -e
./gradlew clean build -x test -x koverVerify
set +e
echo "도커 컴포즈로 앱 시작 중..."
cd mains/monolith-main
docker compose down
docker rmi monolith-main-monolith-main
docker compose up -d
echo "프로젝트 빌드 및 배포 완료."