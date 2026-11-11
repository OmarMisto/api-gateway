package com.example.gateway.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    public JwtFilter(JwtService jwtService){
        this.jwtService=jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri= request.getRequestURI();
        if (uri.startsWith("/auth/v1/")){
            filterChain.doFilter(request,response);
            return;
        }
        String authHeader=request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token =authHeader.substring(7);
        if (jwtService.isTokenValid(token)){
            filterChain.doFilter(request,response);
            return;
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
