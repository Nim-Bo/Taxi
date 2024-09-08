package com.taxiuser.config;

import com.taxiuser.model.User;
import com.taxiuser.service.CustomUserDetailsService;
import com.taxiuser.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtService jwtService;
    CustomUserDetailsService customUserDetailsService;
    RedissonClient redisson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(jwtService.getStartIndex());
        String usernameOrPhone = jwtService.extractSubject(jwt);

        if(usernameOrPhone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            RSetCache<String> signupCache = redisson.getSetCache("signupCache");

            if (signupCache.contains(usernameOrPhone) && jwtService.isTokenNotExpired(jwt)) {
                UsernamePasswordAuthenticationToken signUpAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        usernameOrPhone,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_SIGNUP"))
                );
                signUpAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(signUpAuthenticationToken);
            }
            else {
                User userDetails = customUserDetailsService.loadUserByUsername(usernameOrPhone);
                if (userDetails != null && jwtService.isTokenNotExpired(jwt)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(),
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
