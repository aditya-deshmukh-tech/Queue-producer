package com.queue.sender.queuemessageproducer.common;

import java.util.Map;

public interface RetryChannelInterface {

    void add(Map<String, Object> data);

    boolean isEmpty();

    Map<String, Object> getOne();

    void remove(Map<String, Object> data);
}
