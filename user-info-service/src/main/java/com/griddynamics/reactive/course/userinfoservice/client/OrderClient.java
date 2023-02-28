package com.griddynamics.reactive.course.userinfoservice.client;

import com.griddynamics.reactive.course.userinfoservice.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import static com.griddynamics.reactive.course.userinfoservice.service.ReactiveLoggingUtil.logOnError;
import static com.griddynamics.reactive.course.userinfoservice.service.ReactiveLoggingUtil.logOnNext;

@Service
@Slf4j
public class OrderClient {

    private final WebClient webClient;

    public OrderClient(WebClient.Builder webClientBuilder) {
        HttpClient client = HttpClient.create();

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://order-search-service:8080")
                .build();
    }

    public Flux<Order> getOrderByPhone(String phoneNumber) {
        String uri = UriComponentsBuilder.fromUriString("/orderSearchService/order/phone")
                .queryParam("phoneNumber", phoneNumber)
                .build()
                .toUriString();

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnEach(logOnNext(order -> log.info("Retrieved order: {}", order)))
                .doOnEach(logOnError(e -> log.info("Error retrieving orders", e)));
    }
}
