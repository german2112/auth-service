package com.gt_enterprise.auth_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable Cross-Site Request Forgery protection
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll() // Permit all
                                                                                                     // requests that
                                                                                                     // starts with
                                                                                                     // /auth, this
                                                                                                     // requests does
                                                                                                     // not have to be
                                                                                                     // authenticated
                        .anyRequest().authenticated() // Any other requests must be authenticated
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// With
                                                                                                             // this we
                                                                                                             // avoid
                                                                                                             // creating
                                                                                                             // Http
                                                                                                             // sessions
                                                                                                             // so the
                                                                                                             // app is
                                                                                                             // stateless.
                                                                                                             // The
                                                                                                             // state
                                                                                                             // lives in
                                                                                                             // the jwt
                                                                                                             // token,
                                                                                                             // not in
                                                                                                             // the app.
                                                                                                             // Closely
                                                                                                             // related
                                                                                                             // to CSRF
                                                                                                             // protection
                                                                                                             // being
                                                                                                             // disabled
                                                                                                             // as no
                                                                                                             // session
                                                                                                             // is
                                                                                                             // created.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Indicates
                                                                                                       // that the JWT
                                                                                                       // filter is
                                                                                                       // positioned
                                                                                                       // before the
                                                                                                       // UsernamePasswordAuthenticationFilter
                                                                                                       // in the filter
                                                                                                       // chain.
                                                                                                       // Username and
                                                                                                       // password
                                                                                                       // filter is used
                                                                                                       // just as a
                                                                                                       // placeholder as
                                                                                                       // we are not
                                                                                                       // actually using
                                                                                                       // form login.
        return http.build(); // Builds and returns the configured SecurityFilterChain
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();// Create a singleton instance that is stored in the ApplicationContext to be
                                           // injected anywhere it is needed.
    }

    // Authentication Manager not needed in this project as it it validating a
    // stateless application using JWT. Authentication Manager has its own methods
    // for supporrting authentication.
    /*
     * @Bean
     * public AuthenticationManager authenticationManager(HttpSecurity http) throws
     * Exception {
     * AuthenticationManagerBuilder builder =
     * http.getSharedObject(AuthenticationManagerBuilder.class); // Retrieves
     * // the
     * // AuthenticationManagerBuilder
     * // inside the
     * // httpsecurity
     * // context
     * builder.getSharedObject(AuthenticationManagerBuilder.class).
     * userDetailsService(userDetailsService) // Specifies
     * // the
     * // userdetailsService
     * // to be used
     * // by the
     * // AuthenticationManager.
     * // Tells the
     * // manager
     * // how to
     * // fetch
     * // details
     * // like
     * // username,
     * // password,
     * // etc.
     * .passwordEncoder(bCryptPasswordEncoder()); // Sets the password encoder to be
     * used by the Authentication
     * // manager
     * return builder.build();
     * }
     */
}
