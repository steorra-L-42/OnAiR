package me.onair.main.kafka.consumer;

public class KafkaConsumerConstants {
    // Kafka 토픽 복제본 개수 (REPLICA_COUNT)
    public static final short REPLICATION_FACTOR = 2;

    // 최대 재시도 횟수
    public static final int MAX_ATTEMPT_COUNT = 2;

    // 재시도 간 대기 시간 (밀리초 단위)
    public static final long BACK_OFF_PERIOD = 1000L; // 2초

    // 토픽 복제본 개수 (autoCreateTopicsWith 사용 시)
    public static final int REPLICA_COUNT = 2;
}