package com.example.demo.config;

import com.example.demo.service.UserService;
import com.example.demo.security.JwtUtil;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated());

        // JWT filter
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // simple JWT filter to check Authorization header
    static class JwtAuthenticationFilter extends GenericFilter {
        private final JwtUtil jwtUtil;
        private final UserService userService;

        JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
            this.jwtUtil = jwtUtil;
            this.userService = userService;
        }

        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) req;
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                if (jwtUtil.validate(token)) {
                    String username = jwtUtil.extractUsername(token);
                    var userDetails = userService.loadUserByUsername(username);
                    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            chain.doFilter(req, res);
        }
    }
}