package com.example.loom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CPUHeavyTaskController {

    private static final Logger logger = LoggerFactory.getLogger(CPUHeavyTaskController.class);

    @GetMapping("/prime")
    public void prime() {
        checkPrime();
        logger.info("Thread {} done", Thread.currentThread().threadId());
    }

    private static void checkPrime() {
        long number = 10000008467L;
        for (long i = 2; i <= number; i++) {
            if (number % i == 0) {
                break;
            }
        }
    }
}


