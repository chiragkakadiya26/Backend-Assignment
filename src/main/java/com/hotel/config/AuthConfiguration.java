package com.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Authorize by permissions from Auth0 token
                    .requestMatchers("/api/hotelCreate").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/hotels/*/bookings").access(new WebExpressionAuthorizationManager("hasAnyRole('STAFF','RECEPTION','ADMIN') or hasAuthority('booking:read')"))
                .requestMatchers(HttpMethod.POST, "/api/hotels/*/bookings").access(new WebExpressionAuthorizationManager("hasAnyRole('STAFF','RECEPTION','ADMIN') or hasAuthority('booking:create')"))
                .requestMatchers("/api/hotels/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter((Jwt jwt) -> {
            // Collect authorities from permissions (no prefix)
            JwtGrantedAuthoritiesConverter permissionsConverter = new JwtGrantedAuthoritiesConverter();
            permissionsConverter.setAuthorityPrefix("");
            permissionsConverter.setAuthoritiesClaimName("permissions");

            Collection<GrantedAuthority> merged = new ArrayList<>(permissionsConverter.convert(jwt));

            // Collect authorities from roles claim (prefix as ROLE_)
            JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
            rolesConverter.setAuthorityPrefix("ROLE_");
            rolesConverter.setAuthoritiesClaimName("https://hotel.com/roles");
            merged.addAll(rolesConverter.convert(jwt));

            return merged;
        });
        return converter;
    }
}
