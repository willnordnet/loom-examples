package com.example.loom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IOHeavyTaskController {

    private static final Logger logger = LoggerFactory.getLogger(IOHeavyTaskController.class);

    @GetMapping("/io")
    public void io() throws InterruptedException {
        logger.info("{} thread {} handling i/o task", Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().threadId());

        Thread.sleep(2000);

        logger.info("Thread {} done", Thread.currentThread().threadId());
    }
}


