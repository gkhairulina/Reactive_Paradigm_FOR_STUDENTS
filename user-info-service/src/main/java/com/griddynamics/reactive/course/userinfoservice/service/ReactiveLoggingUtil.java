package com.griddynamics.reactive.course.userinfoservice.service;

import org.slf4j.MDC;
import reactor.core.publisher.Signal;

import java.util.Optional;
import java.util.function.Consumer;

public class ReactiveLoggingUtil {

    public static final String REQUEST_ID = "requestId";

    public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
        return signal -> {
            if (!signal.isOnNext()) {
                return;
            }

            Optional<String> toPutInMdc = signal.getContextView().getOrEmpty(REQUEST_ID);

            toPutInMdc.ifPresentOrElse(requestId -> {
                        try (MDC.MDCCloseable cMdc = MDC.putCloseable(REQUEST_ID, requestId)) {
                            logStatement.accept(signal.get());
                        }
                    },
                    () -> logStatement.accept(signal.get()));
        };
    }

    public static <T> Consumer<Signal<T>> logOnError(Consumer<Throwable> logStatement) {
        return signal -> {
            if (!signal.isOnError()) {
                return;
            }

            Optional<String> toPutInMdc = signal.getContextView().getOrEmpty(REQUEST_ID);

            toPutInMdc.ifPresentOrElse(requestId -> {
                        try (MDC.MDCCloseable cMdc = MDC.putCloseable(REQUEST_ID, requestId)) {
                            logStatement.accept(signal.getThrowable());
                        }
                    },
                    () -> logStatement.accept(signal.getThrowable()));
        };
    }
}
