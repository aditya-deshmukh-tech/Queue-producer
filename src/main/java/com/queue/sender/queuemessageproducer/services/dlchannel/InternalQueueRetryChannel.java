package com.queue.sender.queuemessageproducer.services.dlchannel;

import com.queue.sender.queuemessageproducer.common.RetryChannelInterface;
import org.springframework.stereotype.Component;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Component
public class InternalQueueRetryChannel implements RetryChannelInterface {

    private Queue<Map<String, Object>> retryQueue = new LinkedList<>();

    @Override
    public void add(Map<String, Object> data) {
        retryQueue.add(data);
    }

    @Override
    public boolean isEmpty() {
        return retryQueue.isEmpty();
    }

    @Override
    public Map<String, Object> getOne() {
        return retryQueue.peek();
    }

    @Override
    public void remove(Map<String, Object> data) {
        retryQueue.poll();
    }
}
