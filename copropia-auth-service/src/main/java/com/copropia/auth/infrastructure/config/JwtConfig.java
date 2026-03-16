package com.copropia.auth.infrastructure.config;

import com.copropia.common.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class JwtConfig {

    @Bean
    public JwtUtils jwtUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        log.info("JWT configurado con expiracion de {} ms", expiration);
        return new JwtUtils(secret, expiration);
    }
}
