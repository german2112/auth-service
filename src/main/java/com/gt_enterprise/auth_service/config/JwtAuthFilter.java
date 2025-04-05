package com.gt_enterprise.auth_service.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gt_enterprise.auth_service.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Allows Spring to automatically detect it as a Spring component
public class JwtAuthFilter extends OncePerRequestFilter { // Indicates to use this filter every time a request is
                                                          // received

    @Autowired
    private JwtUtils jwtUtils; // jwt utilities, here we used methods to get the username from the token and
                               // validate the token

    @Autowired
    private UserDetailsService userDetailsService; // Spring security class Used to load user details based on the
                                                   // username extracted from the JWT

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException { // Method that will be called each time a
                                                                               // request is received. Is the core
                                                                               // method of the OncePerRequestFilter
                                                                               // class. It allows us to implement
                                                                               // customl logic to be applied to each
                                                                               // incoming request.


        String jwt = parseJwt(request); // Validate the header and get the token

        if (jwt != null && jwtUtils.validateToken(jwt)) { // validates with the secret key that the token was not
                                                          // altered and that the tokes has not expired
            String username = jwtUtils.getUsernameFromToken(jwt); // Extracts the username from the token
            var userDetails = userDetailsService.loadUserByUsername(username); // Get the user details object associated
                                                                               // with the username (username, password,
                                                                               // roles or permissions).

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( // Creates
                                                                                                               // an
                                                                                                               // authentication
                                                                                                               // object
                                                                                                               // that
                                                                                                               // contains
                                                                                                               // the
                                                                                                               // user
                                                                                                               // details
                                                                                                               // such
                                                                                                               // as
                                                                                                               // username,
                                                                                                               // password,
                                                                                                               // roles
                                                                                                               // or
                                                                                                               // permissions.
                                                                                                               // Which
                                                                                                               // will
                                                                                                               // be
                                                                                                               // used
                                                                                                               // for
                                                                                                               // authorization.
                    userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Sets extra
                                                                                                        // information
                                                                                                        // about the
                                                                                                        // request such
                                                                                                        // as the remote
                                                                                                        // address and
                                                                                                        // session id.

            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Sets the authentication object
                                                                                       // in the security context. From
                                                                                       // this point on, the user is
                                                                                       // already authenticated.
        }
        chain.doFilter(request, response); // Passes the request to the next filter in the chain.
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
