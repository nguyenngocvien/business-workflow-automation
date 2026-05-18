package com.baw.api_gateway.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter scopeAuthoritiesConverter;

    public KeycloakJwtGrantedAuthoritiesConverter() {
        this.scopeAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        Collection<GrantedAuthority> scopeAuthorities = scopeAuthoritiesConverter.convert(jwt);
        if (scopeAuthorities != null) {
            authorities.addAll(scopeAuthorities);
        }

        addRealmRoles(jwt, authorities);
        addClientRoles(jwt, authorities);

        return authorities;
    }

    private static void addRealmRoles(Jwt jwt, List<GrantedAuthority> authorities) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return;
        }

        Object roles = realmAccess.get("roles");
        if (!(roles instanceof Collection<?> roleCollection)) {
            return;
        }

        for (Object role : roleCollection) {
            if (role instanceof String roleName && !roleName.isBlank()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            }
        }
    }

    private static void addClientRoles(Jwt jwt, List<GrantedAuthority> authorities) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return;
        }

        for (Object accessValue : resourceAccess.values()) {
            if (!(accessValue instanceof Map<?, ?> accessMap)) {
                continue;
            }

            Object roles = accessMap.get("roles");
            if (!(roles instanceof Collection<?> roleCollection)) {
                continue;
            }

            for (Object role : roleCollection) {
                if (role instanceof String roleName && !roleName.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                }
            }
        }
    }
}
