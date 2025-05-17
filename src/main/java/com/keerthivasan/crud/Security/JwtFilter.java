package com.keerthivasan.crud.Security;

import com.keerthivasan.crud.Service.UserDetailsServiceImpl;
import com.keerthivasan.crud.Security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this class as a Spring-managed component so it gets picked up during component scanning
@RequiredArgsConstructor // Lombok annotation to generate a constructor for all final fields
public class JwtFilter extends OncePerRequestFilter {

    // Dependency to fetch user details (used to validate JWT)
    private final UserDetailsServiceImpl userDetailsService;

    // Utility class to handle JWT operations like extracting and validating tokens
    private final JwtUtil jwtUtil;

    /**
     * This method is called automatically for each request. It checks for a valid JWT token
     * and sets the authentication in the SecurityContext if the token is valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Get the Authorization header from the incoming request
        final String authHeader = request.getHeader("Authorization");

        String username = null; // To hold extracted username from JWT
        String jwt = null;      // To hold the extracted JWT token

        // Check if the Authorization header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract JWT by removing "Bearer " prefix
            jwt = authHeader.substring(7);

            // Extract username from the JWT token using JwtUtil
            username = jwtUtil.extractUsername(jwt);
        }

        /**
         * Check if username is extracted and there's no existing authentication in the context.
         * This avoids overriding any existing authenticated user (i.e., it ensures idempotency).
         */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details using the provided username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate the token using JwtUtil (to ensure it hasn't expired or been tampered with)
            if (jwtUtil.validateToken(jwt, userDetails)) {

                /**
                 * Create an authentication token using the user's details and authorities.
                 * The password is set to null because the user is already authenticated by JWT.
                 */
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                // Set additional details from the request (like IP address, session ID)
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set this authentication object into the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }

        // Continue the filter chain (i.e., pass the request to the next filter/controller)
        filterChain.doFilter(request, response);
    }
}
