package com.queue.sender.queuemessageproducer.services;

import com.queue.sender.queuemessageproducer.common.RetryChannelInterface;
import com.queue.sender.queuemessageproducer.services.activemq.ActiveMQQueueSender;
import com.queue.sender.queuemessageproducer.services.dlchannel.InternalQueueRetryChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueDataSender extends ActiveMQQueueSender {

    @Autowired
    private InternalQueueRetryChannel internalQueueRetryChannel;

    @Override
    public RetryChannelInterface getRetryChannelInterface() {
        return internalQueueRetryChannel;
    }
}
