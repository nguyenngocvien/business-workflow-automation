package com.baw.identity.infrastructure.web.filter;

import java.io.IOException;
import java.security.Principal;

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
public class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

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
			long durationMillis = (System.nanoTime() - startNanos) / 1_000_000;
			log.info(
				"{} {} -> {} {}ms user={}",
				request.getMethod(),
				getRequestTarget(request),
				response.getStatus(),
				durationMillis,
				getPrincipalName(request.getUserPrincipal())
			);
		}
	}

	private String getRequestTarget(HttpServletRequest request) {
		String queryString = request.getQueryString();
		if (queryString == null || queryString.isBlank()) {
			return request.getRequestURI();
		}
		return request.getRequestURI() + "?" + queryString;
	}

	private String getPrincipalName(Principal principal) {
		return principal == null ? "anonymous" : principal.getName();
	}
}
