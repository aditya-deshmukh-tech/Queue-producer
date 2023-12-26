package com.queue.sender.queuemessageproducer.services.kafka;

import com.queue.sender.queuemessageproducer.common.QueueSenderInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaQueueSender implements QueueSenderInterface {

    @Value("${kafka.topic}")
    private String KAFKA_TOPIC;

    private Queue<Map<String, Object>> DLQueue = new LinkedList<>();

    private Logger kafkaLogger = LogManager.getLogger(KafkaQueueSender.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<?> sendData(Map<String, Object> data) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KAFKA_TOPIC, data);
        return future.whenComplete((result, ex) -> {
            if (ex == null) {
                kafkaLogger.info("pushed to topic succesfully");
            } else {
                kafkaLogger.error("failed to push to topic will retry in sometime",ex);
                persistToRetryDatabase(data);
            }
        });
    }

    public void persistToRetryDatabase(Map<String, Object> data) {
        // for current scenario using queue but will use mongodb database in future here
        DLQueue.add(data);
    }

    //@Scheduled(cron = "0 */5 * ? * *")
    public void retryFromDatabase() {
        // for current scenario using queue but will use mongodb database in future here
        while (!DLQueue.isEmpty()) {
            try {
                kafkaTemplate.send(KAFKA_TOPIC, DLQueue.peek()).get();
                DLQueue.poll();
            } catch (InterruptedException interruptedException) {
                kafkaLogger.error("failed to retry for data : {}", DLQueue.peek(), interruptedException);
            } catch (ExecutionException e) {
                kafkaLogger.error("failed to retry for data : {}", DLQueue.peek(), e);
            }
        }
    }
}
