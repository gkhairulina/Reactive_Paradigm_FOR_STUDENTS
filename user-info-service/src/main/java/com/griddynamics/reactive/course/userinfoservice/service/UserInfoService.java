package com.griddynamics.reactive.course.userinfoservice.service;

import com.griddynamics.reactive.course.userinfoservice.client.OrderClient;
import com.griddynamics.reactive.course.userinfoservice.client.ProductClient;
import com.griddynamics.reactive.course.userinfoservice.domain.Order;
import com.griddynamics.reactive.course.userinfoservice.domain.OrderInfo;
import com.griddynamics.reactive.course.userinfoservice.domain.Product;
import com.griddynamics.reactive.course.userinfoservice.domain.User;
import com.griddynamics.reactive.course.userinfoservice.resource.UserRepository;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;
    private final OrderClient orderClient;
    private final ProductClient productClient;

    public Flux<OrderInfo> getHighestScoreOrderInfo(String userId) {
        return getOrderInfoByFunction(userId, products -> products.reduce((p1, p2) -> p1.getScore() > p2.getScore() ? p1 : p2));
    }

    public Flux<OrderInfo> getOrderInfo(String userId) {
        return getOrderInfoByFunction(userId, Function.identity());
    }

    private Flux<OrderInfo> getOrderInfoByFunction(String userId, Function<Flux<Product>, ? extends Publisher<Product>> productFunction) {
        Mono<User> userMono = userRepository.findUserById(userId);
        Flux<Order> orderFlux = userMono
                .map(User::getPhone)
                .flatMapMany(orderClient::getOrderByPhone);

        return orderFlux.zipWith(userMono, OrderInfo::new)
                .flatMap(orderInfo -> productClient.getProductByCode(orderInfo.getProductCode())
                        .groupBy(Product::getProductCode)
                        .flatMap(productFunction)
                        .map(p -> {
                            orderInfo.setProductName(p.getProductName());
                            orderInfo.setProductId(p.getProductId());
                            return orderInfo;
                        }))
                .log();
    }
}
