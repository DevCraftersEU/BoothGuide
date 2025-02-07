package com.westers.boothguide.security;

import com.westers.boothguide.model.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

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
                .httpBasic(Customizer.withDefaults());

        //Safe for stateless REST
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
