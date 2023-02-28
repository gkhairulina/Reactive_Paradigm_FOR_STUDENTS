package com.griddynamics.reactive.course.userinfoservice.client;

import com.griddynamics.reactive.course.userinfoservice.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

import static com.griddynamics.reactive.course.userinfoservice.service.ReactiveLoggingUtil.logOnError;
import static com.griddynamics.reactive.course.userinfoservice.service.ReactiveLoggingUtil.logOnNext;

@Service
@Slf4j
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(WebClient.Builder webClientBuilder) {
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://product-info-service:8080")
                .build();
    }

    public Flux<Product> getProductByCode(String productCode) {
        String uri = UriComponentsBuilder.fromUriString("/productInfoService/product/names")
                .queryParam("productCode", productCode)
                .build()
                .toUriString();

        return webClient.get().uri(uri)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnEach(logOnNext(product -> log.info("Retrieved product: {}", product)))
                .doOnEach(logOnError(e -> log.info("Error retrieving products", e)))
                .onErrorReturn(new Product());
    }
}
