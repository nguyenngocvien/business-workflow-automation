package com.baw.api_gateway.infrastructure.logging;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayRequestLoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayRequestLoggingFilter.class);

    private final GatewayRouteResolver routeResolver;

    public GatewayRequestLoggingFilter(GatewayRouteResolver routeResolver) {
        this.routeResolver = routeResolver;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startNanos = System.nanoTime();
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
                    String requestPath = exchange.getRequest().getPath().value();
                    String source = resolveClientSource(exchange);
                    String method = exchange.getRequest().getMethod() != null
                            ? exchange.getRequest().getMethod().name()
                            : "UNKNOWN";
                    HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
                    int status = statusCode != null ? statusCode.value() : 200;

                    routeResolver.resolve(requestPath)
                            .ifPresentOrElse(
                                    route -> log.info(
                                            "gateway_request method={} path={} from={} to_service={} target_uri={} status={} duration_ms={}",
                                            method,
                                            requestPath,
                                            source,
                                            route.serviceName(),
                                            route.targetUri(),
                                            status,
                                            durationMs
                                    ),
                                    () -> log.info(
                                            "gateway_request method={} path={} from={} to_service={} target_uri={} status={} duration_ms={}",
                                            method,
                                            requestPath,
                                            source,
                                            "unmatched-route",
                                            "unmatched-route",
                                            status,
                                            durationMs
                                    )
                            );
                });
    }

    private static String resolveClientSource(ServerWebExchange exchange) {
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        String remoteAddr = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : null;
        return Objects.requireNonNullElse(remoteAddr, "unknown-client");
    }
}
