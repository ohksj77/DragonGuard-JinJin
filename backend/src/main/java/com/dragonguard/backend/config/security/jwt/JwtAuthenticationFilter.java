package com.dragonguard.backend.config.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author 김승진
 * @description JWT 토큰의 유효성에 따라 로직을 수행하는 필터 클래스
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private final JwtValidator jwtValidator;

    @Value("${app.auth.token.auth-header}")
    private String tokenTag;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final Optional<String> token = parseBearerToken(request);

        token.ifPresent(
                t -> {
                    Authentication authentication = jwtValidator.getAuthentication(t);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });

        filterChain.doFilter(request, response);
    }

    private Optional<String> parseBearerToken(final HttpServletRequest request) {
        final String bearerToken = request.getHeader(tokenTag);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_HEADER_PREFIX)) {
            return Optional.of(bearerToken.substring(BEARER_HEADER_PREFIX.length()));
        }
        return Optional.empty();
    }
}
