package com.apluslife.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 커스텀 비밀번호 인코더
 * - Admin: 평문 비밀번호 비교
 * - Member: SQL Server PWDCOMPARE 함수 사용
 */
@Component
@Slf4j
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // 인코딩은 하지 않고 평문 그대로 반환
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // rawPassword: 사용자가 입력한 비밀번호
        // encodedPassword: DB에 저장된 비밀번호

        log.debug("비밀번호 검증: rawPassword length={}", rawPassword.length());

        // 평문 비교 (Admin 테이블용)
        if (rawPassword.toString().equals(encodedPassword)) {
            log.debug("평문 비밀번호 일치");
            return true;
        }

        // SQL Server PWDCOMPARE는 CustomUserDetailsService에서 이미 처리됨
        // 여기서는 단순히 false 반환
        log.debug("비밀번호 불일치");
        return false;
    }
}
