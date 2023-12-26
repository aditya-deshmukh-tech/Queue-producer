package com.queue.sender.queuemessageproducer.common;

import com.queue.sender.queuemessageproducer.services.activemq.ActiveMQQueueSender;
import com.queue.sender.queuemessageproducer.services.kafka.KafkaQueueSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class QueueSendService {

//    @Autowired
//    private KafkaQueueSender kafkaQueueSender;

    @Autowired
    private ActiveMQQueueSender activeMQQueueSender;

    public CompletableFuture<?> sendToQueue(Map<String, Object> data, String queueType) {
        switch (queueType) {
//            case "kafka" :
//                return kafkaQueueSender.sendData(data);
            case "activemq" :
                return activeMQQueueSender.sendData(data);
            default :
                throw new IllegalArgumentException("no queue typw specified");
        }
    }
}
