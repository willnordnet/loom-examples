package com.example.loom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class ExampleController {

    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);

    private static final ThreadLocal<String> AUTH_CONTEXT = ThreadLocal.withInitial(() -> null);
    private static final InheritableThreadLocal<String> INHERITABLE_AUTH_CONTEXT = new InheritableThreadLocal<>();

    @GetMapping("/syncIo")
    public void SyncIo() throws InterruptedException {
        logger.info("{} thread {} handling synchronized block", Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().threadId());
        synchronized (this) {
            logger.info("Sleeping in sync block on thread {}", Thread.currentThread().threadId());
            Thread.sleep(2000);
        }
        logger.info("Thread {} done", Thread.currentThread().threadId());
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


