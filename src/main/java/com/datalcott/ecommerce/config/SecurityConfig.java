package com.datalcott.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // ADMIN PRODUCT MANAGEMENT
                        // =========================

                        .requestMatchers(
                                "/products/new",
                                "/products/save"
                        ).hasRole("ADMIN")


                        // =========================
                        // ADMIN CATEGORY MANAGEMENT
                        // =========================

                        .requestMatchers(
                                "/categories/new",
                                "/categories/save"
                        ).hasRole("ADMIN")


                        // =========================
                        // ADMIN PAGES
                        // =========================

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")


                        // =========================
                        // PUBLIC PAGES
                        // =========================

                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/products",
                                "/products/*",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()


                        // =========================
                        // LOGGED-IN USERS
                        // =========================

                        .requestMatchers(
                                "/profile",
                                "/orders/**",
                                "/cart/**",
                                "/wishlist/**",
                                "/reviews/**",
                                "/checkout/**"
                        ).authenticated()


                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest()
                        .authenticated()
                )


                // =========================
                // LOGIN
                // =========================

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/products", true)
                        .permitAll()
                )


                // =========================
                // LOGOUT
                // =========================

                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );


        return http.build();
    }
}