package com.example.loom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class LoomExampleApplication {

    private final Logger logger = LoggerFactory.getLogger(LoomExampleApplication.class);

    void main() {

        SpringApplication.run(LoomExampleApplication.class);
        logger.info("Available processors: {}", Runtime.getRuntime().availableProcessors());

    }

}
