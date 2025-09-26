package com.csi_rscoe.csi_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll() // public endpoints
                        .requestMatchers("/api/auth/**").permitAll() // allow login
                        .requestMatchers("/api/test/**").permitAll() // allow test endpoints
                        .requestMatchers("/api/Admin/Announcements/**").permitAll() // allow announcements
                        .requestMatchers("/uploads/**").permitAll() // allow public access to uploaded files
                        .requestMatchers("/api/admin/**").permitAll() // temporarily allow all admin endpoints
                        .anyRequest().authenticated());

        return http.build();
    }
}
