package com.griddynamics.reactive.course.userinfoservice.service;

import com.griddynamics.reactive.course.userinfoservice.client.OrderClient;
import com.griddynamics.reactive.course.userinfoservice.client.ProductClient;
import com.griddynamics.reactive.course.userinfoservice.domain.Order;
import com.griddynamics.reactive.course.userinfoservice.domain.OrderInfo;
import com.griddynamics.reactive.course.userinfoservice.domain.Product;
import com.griddynamics.reactive.course.userinfoservice.domain.User;
import com.griddynamics.reactive.course.userinfoservice.resource.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.griddynamics.reactive.course.userinfoservice.service.ReactiveLoggingUtil.REQUEST_ID;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
class UserInfoServiceTest {

    private static final String userId = "user1";
    private static final String phone = "phone1";
    private static final String name = "name1";
    private static final String orderNumber = "order1";
    private static final String orderNumber2 = "order2";
    private static final String productCode = "121";
    private static final String productId = "100";
    private static final String productName = "Ice";
    private static final String productId2 = "200";
    private static final String productName2 = "Snow";

    @Mock
    UserRepository userRepository;
    @Mock
    OrderClient orderClient;
    @Mock
    ProductClient productClient;

    @InjectMocks
    UserInfoService userInfoService;

    @Test
    void getHighestOrderInfo() {
        MDC.put(REQUEST_ID, "111111");
        User user = new User(userId, name, phone);
        Order order = new Order(phone, orderNumber, productCode);
        Product product = new Product(productId, productCode, productName, 1.0);
        Product product2 = new Product(productId2, productCode, productName2, 2.0);

        Mockito.when(userRepository.findUserById(userId)).thenReturn(Mono.just(user));
        Mockito.when(orderClient.getOrderByPhone(phone)).thenReturn(Flux.just(order));
        Mockito.when(productClient.getProductByCode(productCode)).thenReturn(Flux.just(product, product2));

        Flux<OrderInfo> orderInfoFlux = userInfoService.getHighestScoreOrderInfo(userId);

        OrderInfo orderInfoExpected = new OrderInfo(order, user);
        orderInfoExpected.setProductId(productId2);
        orderInfoExpected.setProductName(productName2);

        StepVerifier.create(orderInfoFlux)
                .expectNext(orderInfoExpected)
                .verifyComplete();

        MDC.clear();
    }

    @Test
    void getOrderInfo() {
        MDC.put(REQUEST_ID, "222222");
        User user = new User(userId, name, phone);
        Order order = new Order(phone, orderNumber, productCode);
        Order order2 = new Order(phone, orderNumber2, productCode);
        Product product = new Product(productId, productCode, productName, 1.0);
        Product product2 = new Product(productId2, productCode, productName2, 2.0);

        Mockito.when(userRepository.findUserById(userId)).thenReturn(Mono.just(user));
        Mockito.when(orderClient.getOrderByPhone(phone)).thenReturn(Flux.just(order, order2));
        Mockito.when(productClient.getProductByCode(productCode)).thenReturn(Flux.just(product, product2));

        Flux<OrderInfo> orderInfoFlux = userInfoService.getOrderInfo(userId);

        StepVerifier.create(orderInfoFlux)
                .expectNextCount(2)
                .verifyComplete();

        MDC.clear();
    }
}