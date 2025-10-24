package com.apluslife.manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 메인 컨트롤러
 * 관리자 로그인, 관리자 대시보드 등 관리자 페이지 관리
 */
@Slf4j
@Controller
public class AdminMainController {

    /**
     * 관리자 로그인 페이지
     */
    @GetMapping("/admin/login")
    public String adminLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "관리자 아이디 또는 비밀번호가 올바르지 않습니다.");
            log.warn("관리자 로그인 실패 발생");
        }

        if (logout != null) {
            model.addAttribute("message", "로그아웃되었습니다.");
            log.info("관리자 로그아웃 완료");
        }

        return "manager/login";
    }

    /**
     * 관리자 대시보드 페이지
     */
    @GetMapping("/admin")
    public String adminPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        log.info("관리자 페이지 접속: {}", auth.getName());
        return "manager/admin";
    }
}
