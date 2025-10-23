package com.apluslife.config;

import com.apluslife.domain.member.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomPasswordEncoder customPasswordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return customPasswordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(customPasswordEncoder);
        return new ProviderManager(provider);
    }

    /**
     * 관리자 로그인 전용 SecurityFilterChain
     * /admin/** 경로에 대한 별도 인증 처리
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                // 관리자 경로만 처리
                .securityMatcher("/admin/**")

                // AuthenticationManager 설정
                .authenticationManager(authenticationManager)

                // CSRF 설정 - 로그인 폼 제출을 위해 비활성화
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/login", "/admin/logout")
                )

                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()  // 관리자 로그인 페이지는 공개
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // 나머지 관리자 페이지는 ADMIN 권한 필요
                )

                // 관리자 로그인 설정
                // TODO: 현재는 평문 비밀번호 사용 중 - 추후 암호화(BCrypt 등) 적용 필요
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )

                // 관리자 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * 일반 회원 로그인 전용 SecurityFilterChain
     * 관리자 경로를 제외한 나머지 경로 처리
     */
    @Bean
    @Order(2)
    public SecurityFilterChain memberFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                // AuthenticationManager 설정
                .authenticationManager(authenticationManager)
                // CSRF 설정
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")
                )

                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 공개 페이지 - 로그인 없이 접근 가능
                        .requestMatchers("/", "/login", "/h2-console/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/smarteditor/**").permitAll()  // SmartEditor 리소스
                        .requestMatchers("/api/**").permitAll()  // API 접근 허용

                        // 일반 홈페이지 - 로그인 없이 접근 가능
                        .requestMatchers("/about/**").permitAll()          // 회사소개
                        .requestMatchers("/products/**").permitAll()       // 상품소개
                        .requestMatchers("/board/**").permitAll()          // 게시판 (목록 및 상세 조회)

                        // 마이페이지 - USER 권한 필요
                        .requestMatchers("/mypage/**").hasRole("USER")

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 세션 관리 - 30분 타임아웃
                .sessionManagement(session -> session
                        .maximumSessions(1)                    // 동시 세션 1개만 허용
                        .maxSessionsPreventsLogin(false)       // 새 로그인 시 기존 세션 만료
                )

                // 회원 로그인 설정
                // TODO: 현재는 평문 비밀번호 사용 중 - 추후 암호화(BCrypt 등) 적용 필요
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // 회원 로그아웃 설정
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
