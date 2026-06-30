package com.spring.basic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean //uncomment during non-jwt
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))//controls iframe embedding protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/orders/**").hasAnyRole("USER") //No need to give ROLE_USER, internally, it makes it

                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                        //.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Comment it out form based login .By default SessionCreationPolicy.IF_REQUIRED. we have kept it during form based login

                .formLogin(form -> form.defaultSuccessUrl("/orders", true)) // Uncomment it for form based
                //.httpBasic(Customizer.withDefaults()) //We have implemented this later. the only difference in this case will be that no session will be maintained. User needs to send username/password in every request
                ; 
                
 
        return http.build();
    }

    @Bean //un-comment during non-jwt
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) { //UserDetailsService is coming from UserAuthEntityService as it implements it
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder); //passwordEncoder is coming from UserDetailsServiceAutoConfiguration.java
        return provider;
    }
}
