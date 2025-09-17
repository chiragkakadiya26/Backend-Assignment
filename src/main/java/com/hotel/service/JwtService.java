package com.hotel.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {
    String getUserId(Authentication authentication);
    String getUserEmail(Authentication authentication);
    boolean hasRole(Authentication authentication, String role);
}

