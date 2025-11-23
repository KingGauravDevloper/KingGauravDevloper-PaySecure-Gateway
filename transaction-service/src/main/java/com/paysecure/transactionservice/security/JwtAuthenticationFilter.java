package com.paysecure.transactionservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 🔍 DEBUG LOG 1
        System.out.println("🔍 [Filter] Checking Request: " + request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("✅ [Filter] Username extracted: " + username);
            } catch (Exception e) {
                // 🔍 DEBUG LOG 2
                System.out.println("❌ [Filter] Error extracting username: " + e.getMessage());
                e.printStackTrace(); // This will print the full error cause
            }
        } else {
            System.out.println("⚠️ [Filter] No Bearer Token found in header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 🔍 DEBUG LOG 3
            System.out.println("🔍 [Filter] Validating token...");
            
            if (jwtUtil.validateToken(jwt)) {
                System.out.println("✅ [Filter] Token is VALID. Setting SecurityContext.");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        new ArrayList<>()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("❌ [Filter] Token Validation FAILED.");
            }
        }

        filterChain.doFilter(request, response);
    }
}