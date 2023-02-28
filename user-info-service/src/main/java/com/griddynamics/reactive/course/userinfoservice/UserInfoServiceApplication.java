package com.griddynamics.reactive.course.userinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class UserInfoServiceApplication {

    public static void main(String[] args) {
//        ReactorDebugAgent.init();
        SpringApplication.run(UserInfoServiceApplication.class, args);
//        ReactorDebugAgent.processExistingClasses();
    }
}
