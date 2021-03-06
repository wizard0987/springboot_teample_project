package com.webservice.bookstore.config.security;

import com.webservice.bookstore.config.security.auth.CustomUserDetailsService;
import com.webservice.bookstore.config.security.jwt.*;
import com.webservice.bookstore.config.security.logout.handler.CustomLogoutSuccessfulHandler;
import com.webservice.bookstore.config.security.oauth2.CustomOAuth2UserService;
import com.webservice.bookstore.config.security.oauth2.handler.HttpCookieOAuth2AuthorizationRequestRepository;
import com.webservice.bookstore.config.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.webservice.bookstore.config.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.webservice.bookstore.domain.entity.member.MemberRepository;
import com.webservice.bookstore.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .addFilter(corsFilter)
            .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), jwtUtil, redisUtil),
                    UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(), jwtUtil, redisUtil, memberRepository),
                    BasicAuthenticationFilter.class)
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/board/**", "/api/items/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/coupon/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.POST,"/api/coupon/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/admin/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/mypage/**", "/api/cart/**", "/api/order/**").access("hasRole('ROLE_USER')")
                .antMatchers("/api/board/**", "/api/items/**", "/logout").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
                .anyRequest().permitAll()
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 특정 권한만 접근할 수 있는 페이지에 대해 로그인 없이 접근하려고 하면 아래 메소드가 호출
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unable to access without login authentication.");
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 특정 권한만 접근할 수 있는 페이지에 대해 접근 권한이 없는 (인증된) 계정이 접근하려고 하면 아래 메소드가 호출
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have access.");
                    }
                })
                .and()
            .logout()
                .logoutSuccessHandler(new CustomLogoutSuccessfulHandler(redisUtil))
                .and()
            .oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                    .and()
                .redirectionEndpoint()
                    .baseUri("/login/oauth2/code/*")
                    .and()
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)     // OAuth2 인증 성공 시 해당 핸들러 수행
                .failureHandler(oAuth2AuthenticationFailureHandler);    // OAuth2 인증 실패 시 해당 핸들러 수행

    }

}
