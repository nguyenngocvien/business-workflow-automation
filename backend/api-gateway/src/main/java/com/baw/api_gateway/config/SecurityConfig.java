package com.baw.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import com.baw.api_gateway.exception.GatewayAccessDeniedHandler;
import com.baw.api_gateway.exception.GatewayAuthenticationEntryPoint;
import com.baw.api_gateway.security.KeycloakJwtGrantedAuthoritiesConverter;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityFilterChain(
			ServerHttpSecurity http,
			GatewayAuthenticationEntryPoint authenticationEntryPoint,
			GatewayAccessDeniedHandler accessDeniedHandler) {
		http
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.cors(Customizer.withDefaults())
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler))
				.authorizeExchange(auth -> auth
						.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.pathMatchers("/actuator/health", "/actuator/info").permitAll()
						.pathMatchers(
								"/swagger-ui.html",
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/fallback/**",
								"/identity/v3/api-docs",
								"/identity/v3/api-docs/**",
								"/document/v3/api-docs",
								"/document/v3/api-docs/**",
								"/workflow/v3/api-docs",
								"/workflow/v3/api-docs/**",
								"/integration/v3/api-docs",
								"/integration/v3/api-docs/**")
						.permitAll()
						.anyExchange().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(
								jwtAuthenticationConverter())));

		return http.build();
	}

	@Bean
	public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
		ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(
				new ReactiveJwtGrantedAuthoritiesConverterAdapter(
						new KeycloakJwtGrantedAuthoritiesConverter()));
		return converter;
	}
}
