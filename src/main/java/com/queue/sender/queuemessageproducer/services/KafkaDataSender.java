package com.queue.sender.queuemessageproducer.services;

import com.queue.sender.queuemessageproducer.common.RetryChannelInterface;
import com.queue.sender.queuemessageproducer.services.dlchannel.InternalQueueRetryChannel;
import com.queue.sender.queuemessageproducer.services.kafka.KafkaQueueSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaDataSender extends KafkaQueueSender {

    @Autowired
    private InternalQueueRetryChannel internalQueueRetryChannel;

    @Override
    public RetryChannelInterface getRetryChannelInterface() {
        return internalQueueRetryChannel;
    }
}
