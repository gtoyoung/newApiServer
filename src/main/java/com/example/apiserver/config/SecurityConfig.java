package com.example.apiserver.config;

import com.example.apiserver.filter.JwtAuthenticationFilter;
import com.example.apiserver.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

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
                .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/sign").permitAll()
                .antMatchers("/refreshToken").permitAll()
                .antMatchers("/logout").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated() /* 위의 두개를 제외한 모든 경로는 인증을 필요로 한다는 설정 */
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); /* JWT 인증을 위하여 직접 구현한 필터를 UserDetail 필터 전에 실행 */
        return http.build();
    }

    /**
     * Bycrypt encoder
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

