package me.onair.main.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
public class ConsumerErrorsHandler {
    public void postProcessDltMessage(ConsumerRecord<String, String> record,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
                                      @Header(KafkaHeaders.OFFSET) Long offset,
                                      @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage,
                                      @Header(KafkaHeaders.GROUP_ID) String groupId) {

        log.error(
                "[DLT Log] received message='{}' with partitionId='{}', offset='{}', topic='{}', groupId='{}', error='{}'",
                record.value(), partitionId, offset, topic, groupId, errorMessage);
    }

}
