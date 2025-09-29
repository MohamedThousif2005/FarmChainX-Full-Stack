package com.farmchainx.backend.filter;

import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Skip authentication for public endpoints
        if (request.getServletPath().startsWith("/api/auth") || 
            request.getServletPath().startsWith("/api/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract user ID from token (simplified - you should use proper JWT parsing)
            String authHeader = request.getHeader("Authorization");
            Long userId = extractUserIdFromToken(authHeader);
            
            if (userId != null) {
                User user = userService.getUserById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                
                // Check if user is approved and has FARMER role
                if (!user.getApproved()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Account not approved");
                    return;
                }
                
                if (!"FARMER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                    return;
                }
                
                // Add user ID to request for controller use
                request.setAttribute("X-User-ID", userId.toString());
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }

    private Long extractUserIdFromToken(String authHeader) {
        // Simplified implementation - replace with proper JWT parsing
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // In real implementation, decode JWT and extract user ID
            // For now, return a default user ID
            return 1L;
        }
        return null;
    }
}