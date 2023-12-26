package com.queue.sender.queuemessageproducer.services.kafka;

import com.queue.sender.queuemessageproducer.common.QueueSenderInterface;
import com.queue.sender.queuemessageproducer.common.RetryChannelInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class KafkaQueueSender implements QueueSenderInterface {

    @Value("${kafka.topic}")
    private String KAFKA_TOPIC;

    public abstract RetryChannelInterface getRetryChannelInterface();

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
        kafkaLogger.info("persisting to database..");
        getRetryChannelInterface().add(data);
    }

    @Scheduled(fixedRate = 10000)
    public void retryFromDatabase() {
        // for current scenario using queue but will use mongodb database in future here
        kafkaLogger.info("retrying from database...");
        kafkaLogger.info("retry database is empty {}", getRetryChannelInterface().isEmpty());
        while (!getRetryChannelInterface().isEmpty()) {
            try {
                Map<String, Object> data = getRetryChannelInterface().getOne();
                kafkaTemplate.send(KAFKA_TOPIC, data).get();
                getRetryChannelInterface().remove(data);
                kafkaLogger.info("resending data succesfull..");
            } catch (InterruptedException interruptedException) {
                kafkaLogger.error("failed to retry for data ", interruptedException);
            } catch (ExecutionException e) {
                kafkaLogger.error("failed to retry for data ", e);
            }
        }
    }
}
