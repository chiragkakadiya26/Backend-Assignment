package com.hotel.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SupportConfig {

    @Value("${app.support.email}")
    private String supportEmail;

    @PostConstruct
    public void init() {
        System.out.println("Support Email: " + supportEmail);
    }
}
