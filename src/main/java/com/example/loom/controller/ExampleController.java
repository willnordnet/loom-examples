package com.example.loom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class ExampleController {

    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);

    @GetMapping("/hello")
    public String sayHello() throws InterruptedException {
        logger.info("Hello running on {} thread: {}", Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().getName());

        Thread.sleep(2000);

        return "Hello, World!";
    }

    @GetMapping("/run")
    public String structuredTask() throws InterruptedException {
        logger.info("Running on {} thread: {}", Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().getName());

        ScopedValue<String> requestScope = ScopedValue.newInstance();
        ScopedValue.where(requestScope, UUID.randomUUID().toString()).run(() -> {
            logger.info("In request {}", requestScope.get());

            try (var scope = StructuredTaskScope.open()) {
                scope.fork(() -> logger.info("In structured concurrent task 1 with request {}", requestScope.get()));
                scope.fork(() -> logger.info("In structured concurrent task 2 with request {}", requestScope.get()));
                scope.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        Thread.sleep(1000);
        return "Task done";
    }

    @GetMapping(value = "/hello-stream", produces = TEXT_EVENT_STREAM_VALUE)
    public StreamingResponseBody helloStream() {
        var payload = UUID.randomUUID().toString();
        return outputStream -> {
            while (true) {
                logger.info("Streaming payload {} on {} thread: {}", payload, Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().getName());

                String event = "Payload: %s\n\n".formatted(payload);
                outputStream.write(event.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                outputStream.flush();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.warn("Streaming interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
}


