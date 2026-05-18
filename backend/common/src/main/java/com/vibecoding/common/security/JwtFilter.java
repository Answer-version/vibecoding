package com.vibecoding.common.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtFilter implements Filter {

    private final JwtUtils jwtUtils;
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludePaths;

    public JwtFilter(JwtUtils jwtUtils, String... excludePaths) {
        this.jwtUtils = jwtUtils;
        this.excludePaths = Arrays.asList(excludePaths);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        if (isExclude(path)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(401);
            httpResponse.getWriter().write("{\"code\":20001,\"message\":\"Token missing\"}");
            return;
        }

        String token = authHeader.substring(7);
        if (jwtUtils.isExpired(token)) {
            httpResponse.setStatus(401);
            httpResponse.getWriter().write("{\"code\":20002,\"message\":\"Token expired\"}");
            return;
        }

        try {
            var claims = jwtUtils.parseToken(token);
            httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
            httpRequest.setAttribute("userType", claims.get("userType", String.class));
            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.setStatus(401);
            httpResponse.getWriter().write("{\"code\":20001,\"message\":\"Invalid token\"}");
        }
    }

    private boolean isExclude(String path) {
        return excludePaths.stream().anyMatch(p -> pathMatcher.match(p, path));
    }
}