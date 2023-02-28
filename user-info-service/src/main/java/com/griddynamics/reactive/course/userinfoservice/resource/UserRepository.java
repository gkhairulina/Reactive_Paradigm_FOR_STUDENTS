package com.griddynamics.reactive.course.userinfoservice.resource;

import com.griddynamics.reactive.course.userinfoservice.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findUserById(String id);
    Flux<User> findAll();
}
