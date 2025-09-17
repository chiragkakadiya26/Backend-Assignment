package com.hotel.service.impl;

import com.hotel.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class JwtServiceImp implements JwtService {

    @Override
    public String getUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getSubject();
            log.debug("Extracted user ID from JWT: {}", userId);
            return userId;
        }
        log.warn("Failed to extract user ID from authentication - principal is not JWT");
        return null;
    }

    @Override
    public String getUserEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            log.debug("Extracted user email from JWT: {}", email);
            return email;
        }
        log.warn("Failed to extract user email from authentication - principal is not JWT");
        return null;
    }

    @Override
    public boolean hasRole(Authentication authentication, String role) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean hasRole = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.toUpperCase()));
        
        log.debug("User {} has role {}: {}", getUserId(authentication), role, hasRole);
        return hasRole;
    }
}
