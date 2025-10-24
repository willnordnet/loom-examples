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

        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        long maxMem = Runtime.getRuntime().maxMemory();
        logger.info("Memory (MB) used/total/max: {}/{}/{}", usedMem / (1024 * 1024), totalMem / (1024 * 1024), maxMem / (1024 * 1024));

    }

}
