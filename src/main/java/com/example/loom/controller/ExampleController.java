package com.example.loom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
public class ExampleController {

    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);

    private static final ThreadLocal<String> AUTH_CONTEXT = ThreadLocal.withInitial(() -> null);
    private static final InheritableThreadLocal<String> INHERITABLE_AUTH_CONTEXT = new InheritableThreadLocal<>();

    @GetMapping("/thread")
    public void thread() throws InterruptedException {
        logger.info("{} thread {} handling i/o task", Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().threadId());

        Thread.sleep(2000);

        logger.info("Thread {} done", Thread.currentThread().threadId());
    }

    @GetMapping("/prime")
    public void prime() {
        logger.info("{} thread {} handling prime number", Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().threadId());

        long number = 10000008467L;
        for (long i = 2; i <= number; i++) {
            if (number % i == 0) {
                break;
            }
        }

        logger.info("Thread {} done", Thread.currentThread().threadId());
    }

    @GetMapping("/syncThread")
    public void syncThread() throws InterruptedException {
        logger.info("{} thread {} handling synchronized block", Thread.currentThread().isVirtual() ? "virtual" : "platform", Thread.currentThread().threadId());
        synchronized (this) {
            logger.info("Sleeping in sync block on thread {}", Thread.currentThread().threadId());
            Thread.sleep(2000);
        }
        logger.info("Thread {} done", Thread.currentThread().threadId());
    }

    @GetMapping("/threadLocal")
    public void threadLocal(@RequestHeader(value = "Authorization", required = false) String authHeader) throws InterruptedException {

        AUTH_CONTEXT.set(authHeader);
        INHERITABLE_AUTH_CONTEXT.set(authHeader);

        logger.info("Thread local: request with auth header: {}", AUTH_CONTEXT.get() != null ? AUTH_CONTEXT.get() : "no-value");
        logger.info("Thread local: request with auth header: {} in {}", INHERITABLE_AUTH_CONTEXT.get() != null ? INHERITABLE_AUTH_CONTEXT.get() : "no-value", INHERITABLE_AUTH_CONTEXT);

        Thread child = new Thread(() -> {
            logger.info("Child thread with auth header in thread local: {}", AUTH_CONTEXT.get() != null ? AUTH_CONTEXT.get() : "no-value");
            logger.info("Child thread with auth header in inheritable thread local: {} in {}", INHERITABLE_AUTH_CONTEXT.get() != null ? INHERITABLE_AUTH_CONTEXT.get() : "no-value", INHERITABLE_AUTH_CONTEXT);
        });

        child.start();
        child.join();
    }

    @GetMapping("/scopedValue")
    public void scopedValue(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        ScopedValue<String> requestScope = ScopedValue.newInstance();
        ScopedValue.where(requestScope, authHeader).run(() -> {
            logger.info("Request with auth header: {}", requestScope.orElse("no-value"));
        });
    }

    @GetMapping("/run")
    public void structuredTask() throws InterruptedException {
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


