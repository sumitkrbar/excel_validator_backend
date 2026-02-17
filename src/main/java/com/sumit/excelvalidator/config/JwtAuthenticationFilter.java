package com.sumit.excelvalidator.config;

import com.sumit.excelvalidator.service.CustomUserDetailsService;
import com.sumit.excelvalidator.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No JWT token found in request for path: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            logger.debug("JWT token extracted for user: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    logger.info("JWT token validated successfully for user: {}", username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("User {} authenticated and set in security context", username);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            logger.warn("JWT token has expired");
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has expired");
            return;
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token received");
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid token format");
            return;
        } catch (Exception e) {
            logger.error("JWT authentication error: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + e.getMessage());
            return;
        }

    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"message\":\"%s\"}", message)
        );
    }
}
