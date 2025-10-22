package com.apluslife.domain.member.service;

import com.apluslife.domain.admin.entity.Admin;
import com.apluslife.domain.admin.repository.AdminRepository;
import com.apluslife.domain.member.entity.Member;
import com.apluslife.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("로그인 시도: username={}", username);

        // 먼저 관리자 테이블에서 확인
        Optional<Admin> adminOpt = adminRepository.findByAdminId(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            log.debug("관리자 권한 부여: {}", username);

            return User.builder()
                    .username(admin.getAdminId())
                    .password(admin.getAdminPw())
                    .authorities(authorities)
                    .build();
        }

        // 관리자가 아니면 회원 테이블에서 확인
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (member.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            log.debug("관리자 권한 부여 (member 테이블): {}", username);
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.debug("일반 사용자 권한 부여: {}", username);
        }

        return User.builder()
                .username(member.getId())
                .password(member.getPw())
                .authorities(authorities)
                .build();
    }
}
