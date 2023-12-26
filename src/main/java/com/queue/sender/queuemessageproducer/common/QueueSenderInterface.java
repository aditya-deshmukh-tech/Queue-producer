package com.queue.sender.queuemessageproducer.common;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface QueueSenderInterface {

    public CompletableFuture<?> sendData(Map<String, Object> data);

    public void persistToRetryDatabase(Map<String, Object> data);

    public void retryFromDatabase();
}
