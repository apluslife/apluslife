package com.apluslife.web.controller;

import com.apluslife.domain.member.dto.LoginRequest;
import com.apluslife.domain.member.dto.LoginResponse;
import com.apluslife.domain.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 로그인 API
     *
     * @param request 로그인 요청 (gubun, id, pw)
     * @param session HTTP 세션
     * @return 로그인 결과
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        log.info("로그인 요청 - ID: {}, gubun: {}", request.getId(), request.getGubun());

        // 로그인 처리
        LoginResponse response = memberService.login(request, session);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃 API
     *
     * @param session HTTP 세션
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        log.info("로그아웃 요청");
        memberService.logout(session);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    /**
     * 현재 로그인 상태 확인 API
     *
     * @param session HTTP 세션
     * @return 로그인 상태 정보
     */
    @GetMapping("/status")
    public ResponseEntity<?> getLoginStatus(HttpSession session) {
        String id = (String) session.getAttribute("id");

        if (id == null) {
            return ResponseEntity.ok().body(new StatusResponse(false, null, null, null, null));
        }

        return ResponseEntity.ok().body(new StatusResponse(
                true,
                id,
                (String) session.getAttribute("mem_gubun"),
                (String) session.getAttribute("mem_name"),
                (String) session.getAttribute("cust_no")
        ));
    }

    /**
     * 로그인 상태 응답 DTO
     */
    record StatusResponse(
            boolean isLoggedIn,
            String id,
            String memGubun,
            String memName,
            String custNo
    ) {}
}
