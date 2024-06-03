package com.install.global;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.global.security.filter.CustomAuthenticationFilter;
import com.install.global.security.filter.CustomAuthorizationFilter;
import com.install.global.security.filter.JwtTokenExistCheckFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import com.install.global.security.service.JwtService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

  private final AuthenticationProvider authenticationProvider;
  private final AuthenticationManagerBuilder authManagerBuilder;

  private final AuthenticationSuccessHandler authenticationSuccessHandler;
  private final AuthenticationFailureHandler authenticationFailureHandler;
  private final AccessDeniedHandler accessDeniedHandler;

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  /**
   * @param http
   * @return
   * @throws Exception
   *  - 필터 호출 순서 (필터가 등록되는 순서 체크)
   *     - 1. JwtTokenExistCheckFilter.class
   *     - 2. CustomAuthorizationFilter.calss
   *     -----------------------------------------
   *     - 3. CustomAuthenticationFilter.class // 로그인 때만 호출
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement((session) -> session.sessionCreationPolicy(STATELESS))
        .addFilter(getAuthenticationFilter())
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(getAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(getLoginMethodTypeCheckFilter(), CustomAuthorizationFilter.class)
        .authorizeHttpRequests(checkResourceAuth())
        .exceptionHandling(customizer -> customizer.accessDeniedHandler(accessDeniedHandler))
    ;
    return http.build();
  }

  /**
   * - Resource 접근 제어
   */
  private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> checkResourceAuth() {
    return (authorize) -> authorize
        .anyRequest()
        .permitAll();
  }

  /**
   * @return CustomAuthenticationFilter
   *  - 권한 체크 필터
   */
  private CustomAuthorizationFilter getAuthorizationFilter() {
    return new CustomAuthorizationFilter(jwtService, objectMapper);
  }

  /**
   * @return CustomAuthenticationFilter
   *  - 인증 필터
   */
  private CustomAuthenticationFilter getAuthenticationFilter() {
    return new CustomAuthenticationFilter(
        authManagerBuilder, authenticationSuccessHandler, authenticationFailureHandler, objectMapper);
  }

  /**
   * @return CustomAuthenticationFilter
   *  - 로그인 method, url 체크 필터
   */
  private JwtTokenExistCheckFilter getLoginMethodTypeCheckFilter() {
    return new JwtTokenExistCheckFilter(objectMapper);
  }
}
