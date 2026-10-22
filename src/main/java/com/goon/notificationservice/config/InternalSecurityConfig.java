package com.goon.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class InternalSecurityConfig {

    @Bean
    SecurityFilterChain internalApiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/register_fcm_token")
                        .access((authentication, context) -> {
                            Authentication current = authentication.get();
                            if (!(current instanceof JwtAuthenticationToken jwtAuthentication)) {
                                return new AuthorizationDecision(false);
                            }
                            boolean hasScope = jwtAuthentication.getAuthorities().stream()
                                    .anyMatch(authority -> authority.getAuthority().equals("SCOPE_notifications.write"));
                            boolean trustedGateway = "api-gateway".equals(
                                    jwtAuthentication.getToken().getClaimAsString("azp"));
                            return new AuthorizationDecision(hasScope && trustedGateway);
                        })
                        .anyRequest().denyAll())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
                .build();
    }
}
