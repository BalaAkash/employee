package com.employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        DefaultBearerTokenResolver resolver= new DefaultBearerTokenResolver();
        resolver.setBearerTokenHeaderName("X-AZURE-TOKEN");
        http.authorizeHttpRequests(auth-> auth
                .requestMatchers("/employee/**").authenticated().
                requestMatchers("/h2-console/**").permitAll().anyRequest().authenticated())
                .oauth2ResourceServer(oauth2->
                        oauth2.bearerTokenResolver(resolver).jwt(jwt->{}));

        http.csrf(csrf->csrf.disable());
        http.headers(headers->headers.frameOptions(frame-> frame.disable()));
        return http.build();
    }
}
