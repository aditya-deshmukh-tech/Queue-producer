package com.queue.sender.queuemessageproducer.controller;

import com.queue.sender.queuemessageproducer.services.QueueDataSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class QueueSenderController {

    @Autowired
    private QueueDataSender queueDataSender;

    @PostMapping("/to-queue")
    public ResponseEntity<?> postToQueue(@RequestBody HashMap<String, Object> data) {
        queueDataSender.sendData(data);
        return ResponseEntity.status(HttpStatus.CREATED).body("will be pushed to queue");
    }
}
