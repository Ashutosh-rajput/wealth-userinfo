package com.WealthManager.UserInfo.configuration;


import com.WealthManager.UserInfo.security.CustomUserInfoDetailService;
import com.WealthManager.UserInfo.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    private final CustomUserInfoDetailService customUserInfoDetailService;
    private final HandlerExceptionResolver exceptionResolver;
    private final JwtAuthFilter jwtAuthFilter; // Inject JwtAuthFilter here

    @Autowired
    public SecurityConfig(CustomUserInfoDetailService customUserInfoDetailService,
                          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
                          JwtAuthFilter jwtAuthFilter) { // Inject JwtAuthFilter
        this.customUserInfoDetailService = customUserInfoDetailService;
        this.exceptionResolver = exceptionResolver;
        this.jwtAuthFilter = jwtAuthFilter;
    }


    private final String[] SWAGGER_URLS = {
            "/swagger-resources/**", "swagger-ui/**", "/swagger-ui/index.html", "/v3/api-docs/**", "/webjars/**", "/docs"
    };

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserInfoDetailService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/welcome", "/registerUser", "/login", "/login/refreshtoken","/verifyUser").permitAll()
                        .requestMatchers(SWAGGER_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
