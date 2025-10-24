package com.example.loom;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.IntStream;


class LoomExampleApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(LoomExampleApplicationTest.class);

    private final RestClient restClient = RestClient.builder()
        .requestFactory(getHttpComponentsClientHttpRequestFactory())
        .baseUrl("http://localhost:8080").
        build();

    private static SimpleClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        return requestFactory;
    }

    @Test
    void concurrencyTest() throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            IntStream.range(0, 1000).forEach(i -> scope.fork(this::httpCall));
            scope.join();
        }
    }

    private void httpCall() {
        logger.info("Making HTTP call from thread {}", Thread.currentThread().threadId());
        restClient.get()
            .uri("/thread")
            .retrieve()
            .toBodilessEntity();
    }

}
