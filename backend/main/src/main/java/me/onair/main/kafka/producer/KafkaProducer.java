package me.onair.main.kafka.producer;


import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.kafka.enums.Topics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public CompletableFuture<String> sendMessageToKafka(Topics topic, String key, String value) {
    CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
        topic.getName(),
        key,
        value
    );

    return future.thenApply(result -> {
      log.info("레코드 전송 성공 = [{}] with offset=[{}]", value, result.getRecordMetadata().offset());
      return "success";
    }).exceptionally(ex -> {
      log.error("레코드 보낼 수 없음=[{}] due to : {}", value, ex.getMessage());
      return "fail";
    });
  }

}
