package com.pattanayutanachot.jirawat.core.bank.security;

import com.pattanayutanachot.jirawat.core.bank.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final StringRedisTemplate redisTemplate; // Redis for token validation

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, StringRedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails instanceof User user) {
                    // Validate token using Redis storage
                    if (isTokenValidInRedis(user, token) && jwtUtil.validateToken(token, userDetails)) {
                        setAuthentication(userDetails, request);
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Checks if the given JWT is valid by cross-checking in Redis.
     */
    private boolean isTokenValidInRedis(User user, String token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String storedToken = ops.get("jwt:" + user.getId()); // Use userId instead of username
        return token.equals(storedToken);
    }

    /**
     * Sets the authenticated user in Spring Security context.
     */
    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}