package com.queue.sender.queuemessageproducer.services.activemq;

import com.queue.sender.queuemessageproducer.common.QueueSenderInterface;
import com.queue.sender.queuemessageproducer.common.RetryChannelInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ActiveMQQueueSender implements QueueSenderInterface {

    @Value("${activemq.topic}")
    private String ACTIVEMQ_TOPIC;

    public abstract RetryChannelInterface getRetryChannelInterface();

    @Autowired
    private JmsTemplate jmsTemplate;

    private Logger activeMQLogger = LogManager.getLogger(ActiveMQQueueSender.class);


    @Override
    public CompletableFuture<?> sendData(Map<String, Object> data) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            jmsTemplate.convertAndSend(ACTIVEMQ_TOPIC, data);
            activeMQLogger.info("succesfully pushed to queue...{}", data);
            return true;
        })
                .exceptionally(ex -> {
                    activeMQLogger.error("error occurred while pushing to queue {}", ex);
                    persistToRetryDatabase(data);
                    return false;
                });
        return future;
    }

    public void persistToRetryDatabase(Map<String, Object> data) {
        // for current scenario using queue but will use mongodb database in future here
        activeMQLogger.info("persisting to database...");
        getRetryChannelInterface().add(data);
    }

    @Scheduled(fixedRate = 10000)
    public void retryFromDatabase() {
        // for current scenario using queue but will use mongodb database in future here
        activeMQLogger.info("retrying from database...");
        activeMQLogger.info("dlq is empty {}", getRetryChannelInterface().isEmpty());
        while (!getRetryChannelInterface().isEmpty()) {
            try {
                Map<String, Object> data = getRetryChannelInterface().getOne();
                jmsTemplate.convertAndSend(ACTIVEMQ_TOPIC, data);
                getRetryChannelInterface().remove(data);
                activeMQLogger.info("succesfully resend data..");
            } catch (Exception ex) {
                activeMQLogger.error("failed to retry for data ", ex);
                break;
            }
        }
    }
}
