package com.griddynamics.reactive.course.userinfoservice.domain;

import lombok.Data;

@Data
public class OrderInfo {

    private String orderNumber;
    private String userName;
    private String phoneNumber;
    private String productCode;
    private String productName;
    private String productId;

    public OrderInfo(Order order, User user) {
        orderNumber = order.getOrderNumber();
        productCode = order.getProductCode();
        phoneNumber = order.getPhoneNumber();
        userName = user.getName();
    }
}
