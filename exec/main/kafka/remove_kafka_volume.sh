#!/bin/bash

# 관련 볼륨 삭제
echo "관련 Docker 볼륨을 삭제 중입니다..."

# Zookeeper 데이터 볼륨 삭제
docker volume rm kafka_zookeeper_data_1 kafka_zookeeper_data_2 kafka_zookeeper_data_3

# Kafka 데이터 볼륨 삭제
docker volume rm kafka_kafka_data_1 kafka_kafka_data_2 kafka_kafka_data_3

echo "서비스 제거 및 볼륨 삭제가 완료되었습니다."