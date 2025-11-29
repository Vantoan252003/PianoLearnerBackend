package com.piano.learn.PianoLearn.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.piano.learn.PianoLearn.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Only apply to admin pages (not APIs)
        if (requestURI.startsWith("/admin/") && !requestURI.startsWith("/admin/login")) {
            String token = null;

            // Try to get token from cookie first
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("adminToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            // If no token in cookie, try Authorization header
            if (token == null) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            // If still no token, try custom header for admin pages
            if (token == null) {
                token = request.getHeader("X-Admin-Token");
            }

            if (token != null) {
                try {
                    String email = jwtUtil.extractUsername(token);

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userService.loadUserByUsername(email);

                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);

                            System.out.println("Admin authenticated: " + email);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Admin authentication failed: " + e.getMessage());
                }
            } else {
                System.out.println("No admin token found for: " + requestURI);
            }
        }

        filterChain.doFilter(request, response);
    }
}