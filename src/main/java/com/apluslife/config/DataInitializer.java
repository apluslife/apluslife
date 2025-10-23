package com.apluslife.config;

import com.apluslife.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("초기 데이터 확인 시작...");

        // Legacy DB 사용 시 초기 데이터 생성 비활성화
        // Legacy DB에는 이미 10,865명의 회원 데이터가 존재합니다.
        // 테스트 계정이 필요한 경우 SQL로 직접 추가하세요.

        // long memberCount = memberRepository.count();
        // log.info("========================================");
        // log.info("현재 회원 수: {}", memberCount);
        // log.info("Legacy DB 연결 완료!");
        // log.info("========================================");

        // 주의: Legacy DB 스키마에는 manage_level 컬럼이 없습니다.
        // 관리자 권한은 id 필드로 판단합니다 (id='admin' or 'administrator')
    }
}
