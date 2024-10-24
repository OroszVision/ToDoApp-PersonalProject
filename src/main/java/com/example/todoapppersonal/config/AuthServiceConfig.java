package com.example.todoapppersonal.config;

import com.example.todoapppersonal.repository.IAppUserRepository;
import com.example.todoapppersonal.repository.IJwtTokenRepository;
import com.example.todoapppersonal.security.JwtUtil;
import com.example.todoapppersonal.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthServiceConfig {

    @Bean
    public AuthService authService(IAppUserRepository appUserRepository,
                                   IJwtTokenRepository tokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtUtil jwtUtil,
                                   @Lazy AuthenticationManager authenticationManager) {
        return new AuthService(appUserRepository, tokenRepository, passwordEncoder, jwtUtil, authenticationManager);
    }
}

