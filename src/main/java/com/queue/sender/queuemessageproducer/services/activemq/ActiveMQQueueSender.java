package com.queue.sender.queuemessageproducer.services.activemq;

import com.queue.sender.queuemessageproducer.common.QueueSenderInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class ActiveMQQueueSender implements QueueSenderInterface {

    @Value("${activemq.topic}")
    private String ACTIVEMQ_TOPIC;


    private Queue<Map<String, Object>> DLQueue = new LinkedList<>();

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
        DLQueue.add(data);
    }

    @Scheduled(fixedRate = 10000)
    public void retryFromDatabase() {
        // for current scenario using queue but will use mongodb database in future here
        activeMQLogger.info("retrying from database...");
        activeMQLogger.info("dlq is empty {}", DLQueue.isEmpty());
        while (!DLQueue.isEmpty()) {
            try {
                jmsTemplate.convertAndSend(ACTIVEMQ_TOPIC, DLQueue.peek());
                DLQueue.poll();
                activeMQLogger.info("succesfully resend data..");
            } catch (Exception ex) {
                activeMQLogger.error("failed to retry for data : {}", DLQueue.peek(), ex);
                break;
            }
        }
    }
}
