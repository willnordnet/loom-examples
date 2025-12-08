package com.example.loom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;

@RestController
public class ScopedValueController {

    private static final Logger logger = LoggerFactory.getLogger(ScopedValueController.class);

    private static final ThreadLocal<String> THREAD_LOCAL_CONTEXT = ThreadLocal.withInitial(() -> null);
    private static final InheritableThreadLocal<String> INHERITABLE_CONTEXT = new InheritableThreadLocal<>();

    private static final ScopedValue<String> SCOPED_VALUE = ScopedValue.newInstance();


    @PostMapping("/login")
    public void login(@RequestBody String name) {
        logger.info("Before login. Thread local context is {}", THREAD_LOCAL_CONTEXT.get() != null ? THREAD_LOCAL_CONTEXT.get() : "no-value");
//        try {
        THREAD_LOCAL_CONTEXT.set(name);
        logger.info("Thread local: login with context: {}", THREAD_LOCAL_CONTEXT.get());
//        } finally {
//            THREAD_LOCAL_CONTEXT.remove();
//        }
        logger.info("After login. Thread local context is {}", THREAD_LOCAL_CONTEXT.get() != null ? THREAD_LOCAL_CONTEXT.get() : "no-value");

    }

    @GetMapping("/whoami")
    public String whoami() {
        logger.info("In a separate request: Thread local context is {}", THREAD_LOCAL_CONTEXT.get() != null ? THREAD_LOCAL_CONTEXT.get() : "no-value");
        return THREAD_LOCAL_CONTEXT.get();
    }

    @PostMapping("/scoped/login")
    public void scopedLogin(@RequestBody String name) {
        logger.info("Before login. SCOPED_VALUE is {} bounded with value {}", SCOPED_VALUE.isBound() ? "" : "not", SCOPED_VALUE.orElse("no-value"));
        ScopedValue.where(SCOPED_VALUE, name)
            .run(() -> logger.info("In scope. Scoped value: login with context: {}", SCOPED_VALUE.get()));
        logger.info("After login. SCOPED_VALUE is {} bounded with value {}", SCOPED_VALUE.isBound() ? "" : "not", SCOPED_VALUE.orElse("no-value"));
    }

    @GetMapping("/scoped/whoami")
    public String scopedWhoami() {
        logger.info("In a separate request: SCOPED_VALUE is {} bounded with value {}", SCOPED_VALUE.isBound() ? "" : "not", SCOPED_VALUE.orElse("no-value"));
        return SCOPED_VALUE.orElse("no-value");
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
}


