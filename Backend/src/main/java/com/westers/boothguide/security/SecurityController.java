package com.westers.boothguide.security;

import com.westers.boothguide.model.ApplicationUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Profile("!disable-auth")
public class SecurityController {

    @Value("${springdoc.secure}")
    private boolean secureSpringdoc;

    private static final String[] springdocPaths = new String[]{"/v3/api-docs", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"};
    private static final String[] PERMITTED_PATHS = new String[]{"/pub/**"};
    private static final String[] ANY_ROLE = new String[]{"/auth/checkAuth"};
    private static final String[] DESIGNER_PATHS = new String[]{"/auth/design"};
    private static final String[] MODERATOR_PATHS = new String[]{"/auth/exhibitors/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!secureSpringdoc) {
            http.authorizeHttpRequests(authorize -> authorize.requestMatchers(springdocPaths).permitAll());
        }
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMITTED_PATHS).permitAll()
                        .requestMatchers(ANY_ROLE).authenticated()
                        .requestMatchers(DESIGNER_PATHS).hasAnyRole(ApplicationUser.Role.DESIGNER.toString(), ApplicationUser.Role.ADMIN.toString())
                        .requestMatchers(MODERATOR_PATHS).hasAnyRole(ApplicationUser.Role.MODERATOR.toString(), ApplicationUser.Role.ADMIN.toString())
                        .anyRequest().hasRole(ApplicationUser.Role.ADMIN.toString())
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(configurer -> configurer.authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint()));

        //Safe for stateless REST
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    /**
     * A custom implementation of {@link AuthenticationEntryPoint} that prevents
     * pop-up authentication dialogs in web browsers by sending an unauthorized
     * error response.
     *
     * <p><strong>Important:</strong> This helps to prevent CSRF attacks when accessing the api directly in the browser.</p>
     *
     * <p>While CSRF is generally not an issue for stateless REST APIs, there is one edge case to consider.
     * In this application, the frontend authenticates requests by adding the authorization header.
     * Since the browser itself does not store authentication data in this setup, clicking a link to the application
     * does not trigger automatically authenticated requests — a prerequisite for CSRF attacks.</p>
     *
     * <p>However, if the API is accessed directly (without the frontend), a browser authentication pop-up for basic auth
     * could appear. If the user provides credentials there, the browser may cache them and automatically include them in
     * future API requests. This behavior could re-enable the possibility of CSRF attacks.
     * Preventing the authentication pop-up mitigates this risk.</p>
     */
    private static class NoPopupBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
    }
}
