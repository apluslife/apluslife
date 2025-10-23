package com.apluslife.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 커스텀 비밀번호 인코더
 *
 * TODO: 보안 강화 필요 - 현재 평문 비밀번호 사용 중
 * 추후 BCrypt, PBKDF2, SCrypt 등의 암호화 알고리즘 적용 권장
 *
 * 현재 구현:
 * - Admin: DB에 평문 저장, 평문 비교
 * - Member: DB에 SQL Server PWDENCRYPT로 암호화 저장, PWDCOMPARE로 비교
 *
 * 작동 방식:
 * 1. Spring Security Form 로그인 시:
 *    - CustomUserDetailsService가 DB에서 사용자 조회
 *    - UserDetails에 DB의 비밀번호를 그대로 저장
 *    - 이 인코더의 matches()가 호출되어 비밀번호 검증
 *
 * 2. API 로그인(/api/member/login) 시:
 *    - MemberService에서 직접 Repository 쿼리로 검증
 *    - 이 인코더는 사용되지 않음
 */
@Component
@Slf4j
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // TODO: 추후 암호화 적용 시 수정 필요
        // 현재: 평문 그대로 반환
        // 예시: return new BCryptPasswordEncoder().encode(rawPassword);
        log.debug("패스워드 인코딩 (평문 유지): length={}", rawPassword.length());
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // rawPassword: 사용자가 입력한 비밀번호
        // encodedPassword: DB에 저장된 비밀번호

        if (rawPassword == null || encodedPassword == null) {
            log.warn("비밀번호가 null입니다: rawPassword={}, encodedPassword={}",
                    rawPassword != null, encodedPassword != null);
            return false;
        }

        log.debug("비밀번호 검증 시작: rawPassword length={}, encodedPassword length={}",
                rawPassword.length(), encodedPassword.length());

        // TODO: 추후 암호화 적용 시 수정 필요
        // 현재: 평문 직접 비교
        // 예시: return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);

        boolean isMatch = rawPassword.toString().equals(encodedPassword);

        if (isMatch) {
            log.debug("비밀번호 일치 (평문 비교)");
        } else {
            log.debug("비밀번호 불일치");
        }

        return isMatch;
    }
}
