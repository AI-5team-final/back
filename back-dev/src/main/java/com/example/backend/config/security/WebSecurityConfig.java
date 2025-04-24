package com.example.backend.config.security;

import com.example.backend.config.jwt.JwtAuthFilter;
import com.example.backend.config.jwt.JwtExceptionFilter;
import com.example.backend.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
//        CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
//        handler.setCsrfRequestAttributeName(CsrfToken.class.getName());

        JsonUsernamePasswordAuthenticationFilter jsonFilter = new JsonUsernamePasswordAuthenticationFilter(authenticationManager, jwtUtil);
        jsonFilter.setAuthenticationSuccessHandler(successHandler);
        jsonFilter.setAuthenticationFailureHandler(failureHandler);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 프론트가 읽을 수 있어야 하니까 false
//                        .csrfTokenRequestHandler(handler)
//                        .ignoringRequestMatchers(
//                                "/auth/login",
//                                "/auth/signup",
//                                "/auth/check-email",
//                                "/front-error",
//                                "/payments/**"
//                        )
//                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/uploads/**","/webhook-front","/webhook-back","/files/**").permitAll()
                        .requestMatchers(
                                "/auth/login", "/auth/signup", "/auth/token/logout","/auth/check-email", "/auth/token/refresh", "/auth/token/me",
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**",
                                "/v3/api-docs/**",
                                "/api/**", "/front-error",
                                "http://221.148.97.237:8080/**"
                            ).permitAll()
                        .requestMatchers(
                                "로그인 해야되는 곳들"
                        ).authenticated()
                        .requestMatchers(
                                "관리자만 접근할 수 있는 곳들"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class); // json로그인 처리 - /auth/login (3)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //JWT 검증 및 SecurityContext 등록 -> 로그인 이후 요청에 필요함.(2)
        http.addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);  //JWT 오류 (토큰 만료, 위조 등) 잡기 -> 제일 먼저 실행됨(1)

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 반드시 true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
