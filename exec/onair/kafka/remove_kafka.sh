#!/bin/bash

# 서비스 이름 배열 설정
SERVICES=(
  "onair_zookeeper-1"
  "onair_zookeeper-2"
  "onair_zookeeper-3"
  "onair_kafka-1"
  "onair_kafka-2"
  "onair_kafka-3"
  "onair_kafka-init"
)

# 각 서비스 제거
for SERVICE in "${SERVICES[@]}"; do
  echo "서비스 '$SERVICE'을(를) 제거 중입니다..."
  docker service rm "$SERVICE"
done

# 관련 볼륨 삭제
echo "관련 Docker 볼륨을 삭제 중입니다..."

# Zookeeper 데이터 볼륨 삭제
docker volume rm onair_zookeeper_data_1 onair_zookeeper_data_2 onair_zookeeper_data_3

# Kafka 데이터 볼륨 삭제
docker volume rm onair_kafka_data_1 onair_kafka_data_2 onair_kafka_data_3

echo "서비스 제거 및 볼륨 삭제가 완료되었습니다."