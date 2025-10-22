package com.apluslife.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomPasswordEncoder customPasswordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return customPasswordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 설정
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
                )

                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 공개 페이지 - 로그인 없이 접근 가능
                        .requestMatchers("/", "/login", "/h2-console/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/**").permitAll()  // API 접근 허용

                        // 일반 홈페이지 - 로그인 없이 접근 가능
                        .requestMatchers("/about/**").permitAll()          // 회사소개
                        .requestMatchers("/products/**").permitAll()       // 상품소개
                        .requestMatchers("/board/**").permitAll()          // 게시판 (목록 및 상세 조회)

                        // 마이페이지 - USER 권한 필요 (ADMIN도 USER 권한 포함)
                        .requestMatchers("/mypage/**").hasAnyRole("USER", "ADMIN")

                        // 관리자 페이지 - ADMIN 권한만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 세션 관리 - 30분 타임아웃
                .sessionManagement(session -> session
                        .maximumSessions(1)                    // 동시 세션 1개만 허용
                        .maxSessionsPreventsLogin(false)       // 새 로그인 시 기존 세션 만료
                )

                // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // H2 콘솔을 위한 프레임 옵션 비활성화
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}
