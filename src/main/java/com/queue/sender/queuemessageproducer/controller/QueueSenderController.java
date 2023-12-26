package com.queue.sender.queuemessageproducer.controller;

import com.queue.sender.queuemessageproducer.common.QueueSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class QueueSenderController {

    @Autowired
    private QueueSendService queueSendService;

    @PostMapping("/to-queue")
    public ResponseEntity<?> postToQueue(@RequestBody HashMap<String, Object> data, @RequestParam("type") String queueType) {
        queueSendService.sendToQueue(data, queueType);
        return ResponseEntity.status(HttpStatus.CREATED).body("will be pushed to queue");
    }
}
