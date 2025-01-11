package gr.aueb.cf.inventorymanagementsystem.security;


import gr.aueb.cf.inventorymanagementsystem.authentication.JwtAuthenticationFilter;
import gr.aueb.cf.inventorymanagementsystem.core.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChainOld(HttpSecurity http) throws Exception {
        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exceptions) -> exceptions
                        .accessDeniedHandler(myCustomAccessDeniedHandler()))
                .exceptionHandling((exceptions) -> exceptions.authenticationEntryPoint(myCustomAuthenticationEntryPoint()))
                .authorizeHttpRequests(req -> req
                                .requestMatchers("/api/auth/authenticate").permitAll() // Ειδικό για την αυθεντικοποίηση
                                .requestMatchers("/api/categories/getAll").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name()) // USER για categories (ειδικός)
                                .requestMatchers("/api/categories/all").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name()) // USER για categories (ειδικός)
                                .requestMatchers("/api/suppliers/getAll").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name()) // USER για suppliers (ειδικός)
                                .requestMatchers("/api/suppliers/all").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name()) // USER για suppliers (ειδικός)
                                .requestMatchers("/api/categories/**").hasAnyAuthority(Role.ADMIN.name()) // ADMIN για categories
                                .requestMatchers("/api/suppliers/**").hasAnyAuthority(Role.ADMIN.name()) // ADMIN για suppliers
                                .requestMatchers("/api/products/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name()) // USER/ADMIN για products
                                .requestMatchers("/api/orders/**").hasAnyAuthority(Role.USER.name(), Role.ADMIN.name()) // USER/ADMIN για orders
                                .requestMatchers("/**").permitAll() // Static resources ή οτιδήποτε άλλο
                        //.authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
        //return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AccessDeniedHandler myCustomAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint myCustomAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

}
