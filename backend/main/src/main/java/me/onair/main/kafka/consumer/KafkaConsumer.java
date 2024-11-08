package me.onair.main.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import me.onair.main.kafka.enums.Topics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    // Value 값만 Consume
//    @KafkaListener(topics = Topics.NAMES.TEST)
//    public void consumeTestTopic(String message) {
//        log.info("[consume message]: {}", message);
//    }

    // Key - Value Consume
//    @KafkaListener(topics = Topics.NAMES.TEST)
//    public void consumeTestTopicWithKey(ConsumerRecord<String, String> record) {
//        log.info("[consume message]: key - {}, value - {}", record.key(), record.value());
//    }

    // Key - Value Consume (Multi-thread)
    @KafkaListener(topics = Topics.NAMES.TEST, concurrency = "3")
    public void consumeTestTopicWithThreads(ConsumerRecord<String, String> record) {
        long startTime = System.currentTimeMillis();

        // 현재 스레드 정보 가져오기
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();

        // 메시지 처리 로직
        log.info("[consume message]: key - {}, value - {}, thread - {}, thread ID - {}, offset - {}",
                record.key(), record.value(), threadName, threadId, record.offset());

        // 메시지 처리 소요 시간 계산
        long endTime = System.currentTimeMillis(); // 끝 시간 기록
        log.info("Processing time for thread {} (ID: {}): {} ms", threadName, threadId, (endTime - startTime));

        // Kafa Dead Letter Topic 테스트
//        throw new IllegalArgumentException();
    }
}
