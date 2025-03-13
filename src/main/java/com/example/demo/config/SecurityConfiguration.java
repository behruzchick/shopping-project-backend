package com.example.demo.config;

import com.example.demo.security.JWTFilter;
import com.example.demo.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final TokenProvider tokenProvider;
    private JWTFilter jwtFilter;
    private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    public SecurityConfiguration(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:5000"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jwtFilter) throws Exception {
        try {

            http
                    .cors(cors -> {})
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterBefore(jwtFilter , UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/user/register").permitAll()
                            .requestMatchers("/api/user/login").permitAll()
                            .requestMatchers("/api/user/account").permitAll()
                            .requestMatchers("/api/user/all").hasRole("ADMIN")
                            .requestMatchers("/api/user/delete").hasRole("ADMIN")


                            .requestMatchers("/api/product/all").permitAll()
                            .requestMatchers("/api/product/get/**").permitAll()
                            .requestMatchers("/api/product/filterByName").permitAll()
                            .requestMatchers("/api/product/lowPrice").permitAll()
                            .requestMatchers("/api/product/topPrice").permitAll()
                            .requestMatchers("/api/product/topRated").permitAll()
                            .requestMatchers("/api/product/voteRate/**").permitAll()
                            .requestMatchers("/api/product/voteRate/**").permitAll()
                            .requestMatchers("/api/product/update/").hasRole("ADMIN")
                            .requestMatchers("/api/product/delete/").hasRole("ADMIN")
                            .requestMatchers("/api/product/create").hasRole("ADMIN")


                            .requestMatchers("/api/images/**").permitAll()
                            .requestMatchers("/api/cart-items/**").permitAll()
                            .anyRequest().authenticated()

                    );
            return http.build();
        } catch (Exception e) {
            throw e;
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}