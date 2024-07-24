package com.example.apiserver.config;

import com.example.apiserver.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .httpBasic().disable()
                .csrf().disable() /* REST API 서버 이르모 basic auth 및 csrf 보안 미사용 */
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) /* JWT를 사용하기 때문에 세션 미사용 */
                .and()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/newsList").authenticated()           // 인증 필요
                                .antMatchers("/webjars/**").permitAll()         // 인증 불필요
                                .anyRequest().permitAll()                        // 모든 URL 허용
                )
                .addFilterBefore(new AuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); /* JWT 인증을 위하여 직접 구현한 필터를 UserDetail 필터 전에 실행 */
        return http.build();
    }

}