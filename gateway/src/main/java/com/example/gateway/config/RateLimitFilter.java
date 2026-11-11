package com.example.gateway.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {
    private final LettuceBasedProxyManager<String> lettuceBasedProxyManager;
    public RateLimitFilter(LettuceBasedProxyManager<String> lettuceBasedProxyManager){
        this.lettuceBasedProxyManager=lettuceBasedProxyManager;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientKey=request.getRemoteAddr();
        BucketConfiguration config= BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillGreedy(10, Duration.ofMinutes(1))
                        .build())
                .build();
        Bucket bucket= lettuceBasedProxyManager.builder().build(clientKey, config);
        if (bucket.tryConsume(1)){
            filterChain.doFilter(request,response);
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }
}
