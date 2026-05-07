package com.baw.api_gateway.infrastructure.logging;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.request-logging")
public record GatewayRouteMappingProperties(
        List<RouteMapping> routes
) {

    public GatewayRouteMappingProperties {
        routes = routes == null ? List.of() : List.copyOf(routes);
    }

    public record RouteMapping(
            String pathPrefix,
            String serviceName,
            String targetUri
    ) {

        public RouteMapping {
            pathPrefix = normalizePrefix(pathPrefix);
            serviceName = serviceName == null ? "unknown-service" : serviceName.trim();
            targetUri = targetUri == null ? "unknown-target" : targetUri.trim();
        }

        private static String normalizePrefix(String prefix) {
            if (prefix == null || prefix.isBlank()) {
                return "/";
            }

            String normalized = prefix.trim();
            if (!normalized.startsWith("/")) {
                normalized = "/" + normalized;
            }
            if (normalized.length() > 1 && normalized.endsWith("/")) {
                normalized = normalized.substring(0, normalized.length() - 1);
            }
            return normalized;
        }
    }
}
