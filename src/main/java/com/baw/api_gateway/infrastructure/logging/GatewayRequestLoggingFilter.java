package com.baw.api_gateway.infrastructure.logging;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayRequestLoggingFilter.class);

    private final GatewayRouteResolver routeResolver;

    public GatewayRequestLoggingFilter(GatewayRouteResolver routeResolver) {
        this.routeResolver = routeResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startNanos = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            String requestPath = request.getRequestURI();
            String source = resolveClientSource(request);
            String method = request.getMethod();
            int status = response.getStatus();

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
        }
    }

    private static String resolveClientSource(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        String remoteAddr = request.getRemoteAddr();
        return Objects.requireNonNullElse(remoteAddr, "unknown-client");
    }
}
