package com.griddynamics.reactive.course.userinfoservice.controller;

import com.griddynamics.reactive.course.userinfoservice.domain.OrderInfo;
import com.griddynamics.reactive.course.userinfoservice.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import static com.griddynamics.reactive.course.userinfoservice.service.ReactiveLoggingUtil.REQUEST_ID;

@RestController
@RequestMapping("/userInfoService")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping(value = "/orders/userId", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrderInfo> getOrdersByUserId(@RequestParam String userId,
                                             @RequestHeader(REQUEST_ID) String requestId) {
        return userInfoService
                .getOrderInfo(userId)
                .contextWrite(Context.of(REQUEST_ID, requestId));
    }

    @GetMapping(value = "/order/userId", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrderInfo> getHighestScoreOrderByUserId(@RequestParam String userId,
                                                        @RequestHeader(REQUEST_ID) String requestId) {
        return userInfoService
                .getHighestScoreOrderInfo(userId)
                .contextWrite(Context.of(REQUEST_ID, requestId));
    }
}
