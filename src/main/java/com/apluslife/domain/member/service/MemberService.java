package com.apluslife.domain.member.service;

import com.apluslife.domain.admin.dto.LoginAdminDto;
import com.apluslife.domain.admin.repository.AdminRepository;
import com.apluslife.domain.member.dto.LoginMemberDto;
import com.apluslife.domain.member.dto.LoginRequest;
import com.apluslife.domain.member.dto.LoginResponse;
import com.apluslife.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    /**
     * 통합 로그인 처리
     * 1. admin 테이블에서 평문 비밀번호로 조회 시도
     * 2. 없으면 member 테이블에서 암호화된 비밀번호로 조회 시도
     *
     * @param request 로그인 요청 정보
     * @param session HTTP 세션
     * @return 로그인 결과
     */
    public LoginResponse login(LoginRequest request, HttpSession session) {
        try {
            // 1. admin 테이블에서 평문 비밀번호로 조회 시도
            LoginAdminDto admin = adminRepository
                    .findByAdminIdAndPassword(request.getId(), request.getPw())
                    .orElse(null);

            if (admin != null) {
                // 관리자 로그인 성공
                log.info("관리자 로그인 성공 - 관리자: {}, 레벨: {}", admin.getAdminName(), admin.getManageLevel());

                // 세션에 관리자 정보 저장
                session.setAttribute("id", admin.getAdminId());
                session.setAttribute("mem_gubun", "admin");
                session.setAttribute("mem_name", admin.getAdminName());
                session.setAttribute("manage_level", admin.getManageLevel());
                session.setAttribute("cust_no", "");

                return LoginResponse.success(
                        1,  // result: 1 = 로그인 성공
                        0,  // 관리자는 비밀번호 변경 경과일자 0
                        "admin",
                        admin.getAdminName(),
                        "",
                        admin.getAdminId()
                );
            }

            // 2. admin 테이블에 없으면 member 테이블에서 조회 (PWDCOMPARE 사용)
            return loginMember(request, session);

        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            return LoginResponse.fail("ERROR_500", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 일반회원 로그인 처리 (member 테이블)
     */
    private LoginResponse loginMember(LoginRequest request, HttpSession session) {
        // 회원 로그인 쿼리 실행
        LoginMemberDto member = memberRepository
                .findByIdAndPassword(request.getId(), request.getPw())
                .orElse(null);

        // 1. 로그인 실패 (ID 또는 PW 오류)
        if (member == null) {
            log.warn("회원 로그인 실패 - 아이디 또는 비밀번호 오류: {}", request.getId());
            return LoginResponse.fail("ERROR_003", "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 2. 탈퇴 회원
        if ("2".equals(member.getWithdraw()) || "Y".equals(member.getWithdraw())) {
            log.warn("로그인 실패 - 탈퇴 회원: {}", request.getId());
            return LoginResponse.success(2, 0, "", "", "", request.getId());
        }

        // 3. 로그인 성공
        log.info("회원 로그인 성공 - 회원: {}, 회원구분: {}", member.getMemberName(), member.getMemberGubun());

        // 세션에 회원 정보 저장
        session.setAttribute("id", member.getId());
        session.setAttribute("mem_gubun", member.getMemberGubun());
        session.setAttribute("mem_name", member.getMemberName());
        session.setAttribute("cust_no", member.getCustNo());

        // 비밀번호 변경 경과일자
        int dateOfDiff = member.getDateOFdiff() != null ? member.getDateOFdiff() : 0;

        return LoginResponse.success(
                1,  // result: 1 = 로그인 성공
                dateOfDiff,
                member.getMemberGubun(),
                member.getMemberName(),
                member.getCustNo() != null ? member.getCustNo() : "",
                member.getId()
        );
    }

    /**
     * 로그아웃 처리
     *
     * @param session HTTP 세션
     */
    public void logout(HttpSession session) {
        session.invalidate();
        log.info("로그아웃 완료");
    }
}
