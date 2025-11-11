package com.example.loom;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.concurrent.StructuredTaskScope;
import java.util.stream.IntStream;


class LoomExampleApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(LoomExampleApplicationTest.class);

    private final RestTestClient restTestClient = RestTestClient.bindToServer()
        .baseUrl("http://localhost:8080")
        .build();

    @Test
    void concurrencyTest() throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            IntStream.range(0, 1000).forEach(i -> scope.fork(this::httpCall));
            scope.join();
        }
    }

    private void httpCall() {
        logger.info("Making HTTP call from thread {}", Thread.currentThread().threadId());
        restTestClient.get()
            .uri("/io")
            .exchange()
            .expectStatus().isOk();
    }

}
