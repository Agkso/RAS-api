package com.example.RAS_API.config;

import com.example.RAS_API.entity.EnumRole;
import com.example.RAS_API.security.JwtAuthenticationFilter;
import com.example.RAS_API.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Para usar @PreAuthorize nos controllers
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll() // Endpoints de autenticação públicos
                        .requestMatchers(HttpMethod.POST, "/api/denuncias").hasAnyRole(EnumRole.USUARIO.name().replace("ROLE_", ""), EnumRole.ADMIN.name().replace("ROLE_", ""), EnumRole.VIGILANTE.name().replace("ROLE_", ""))
                        .requestMatchers(HttpMethod.GET, "/api/denuncias/minhas").hasRole(EnumRole.USUARIO.name().replace("ROLE_", ""))
                        .requestMatchers(HttpMethod.GET, "/api/denuncias", "/api/denuncias/{id}").hasAnyRole(EnumRole.ADMIN.name().replace("ROLE_", ""), EnumRole.VIGILANTE.name().replace("ROLE_", ""))
                        .requestMatchers(HttpMethod.PUT, "/api/denuncias/{id}/status").hasAnyRole(EnumRole.ADMIN.name().replace("ROLE_", ""), EnumRole.VIGILANTE.name().replace("ROLE_", ""))
                        .anyRequest().authenticated() // Todas as outras requisições precisam de autenticação
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}