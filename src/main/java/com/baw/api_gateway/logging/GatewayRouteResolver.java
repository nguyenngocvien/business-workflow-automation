package com.baw.api_gateway.logging;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class GatewayRouteResolver {

    private final List<GatewayRouteMappingProperties.RouteMapping> routeMappings;

    public GatewayRouteResolver(GatewayRouteMappingProperties properties) {
        this.routeMappings = properties.routes().stream()
                .map(GatewayRouteMappingProperties.RouteMapping.class::cast)
                .sorted(Comparator.comparingInt(
                        (GatewayRouteMappingProperties.RouteMapping mapping) -> mapping.pathPrefix().length())
                        .reversed())
                .toList();
    }

    public Optional<ResolvedRoute> resolve(String requestPath) {
        if (requestPath == null || requestPath.isBlank()) {
            return Optional.empty();
        }

        return routeMappings.stream()
                .filter(mapping -> matches(requestPath, mapping.pathPrefix()))
                .findFirst()
                .map(mapping -> new ResolvedRoute(mapping.serviceName(), mapping.targetUri(), mapping.pathPrefix()));
    }

    private static boolean matches(String requestPath, String prefix) {
        return requestPath.equals(prefix) || requestPath.startsWith(prefix + "/");
    }

    public record ResolvedRoute(String serviceName, String targetUri, String pathPrefix) {
    }
}
