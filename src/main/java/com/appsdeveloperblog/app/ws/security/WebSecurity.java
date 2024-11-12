package com.appsdeveloperblog.app.ws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.io.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class WebSecurity {

    private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public WebSecurity(UserService userDetailsService,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserRepository userRepository
    ) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Customize Login URL path
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/users/login");

        // allow post method on users, because user can not be authenticated before creating account
        http
                .csrf(
                        AbstractHttpConfigurer::disable).authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
                        .permitAll()
                        .anyRequest().authenticated())
                        .authenticationManager(authenticationManager)
                        .addFilter(authenticationFilter)
                        .addFilter(new AuthorizationFilter(authenticationManager))
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }
}
