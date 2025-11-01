package com.piano.learn.PianoLearn.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.piano.learn.PianoLearn.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter) { 
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Static resources - allowed without authentication
                .requestMatchers("/admin-script.js", "/css/**", "/js/**", "/images/**").permitAll()
                // Public endpoints - login and register
                .requestMatchers("/", "/index", "/api/auth/login", "/api/auth/register", "/api/auth/checkmail", "/admin/login", "/api/auth/ranking").permitAll()
                // Admin view pages - allow access (token check done in JavaScript)
                .requestMatchers("/admin/dashboard", "/admin/users", "/admin/courses", "/admin/lessons", 
                                "/admin/exercises", "/admin/songs", "/admin/achievements", "/admin/user-progress",
                                "/admin/exercise-results", "/admin/user-achievements", "/admin/practice-sessions",
                                "/admin/daily-goals", "/admin/favorites", "/admin/piano-questions").permitAll()
                // Admin API endpoints - require ADMIN role
                .requestMatchers("/api/admin/**").hasAuthority("admin")
                // Protected endpoints - require authentication
                .requestMatchers("/api/auth/courses/**").authenticated()
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userService);     
        p.setPasswordEncoder(passwordEncoder()); 
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
