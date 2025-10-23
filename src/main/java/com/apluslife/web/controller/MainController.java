package com.apluslife.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class MainController {

    // ========================================
    // 공개 페이지 - web 폴더 (로그인 불필요)
    // ========================================

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities());
            log.info("로그인된 사용자 접속: {}, 권한: {}", auth.getName(), auth.getAuthorities());
        }

        return "web/index";
    }

    /**
     * 회원 로그인 페이지
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            log.warn("회원 로그인 실패 발생");
        }

        if (logout != null) {
            model.addAttribute("message", "로그아웃되었습니다.");
            log.info("로그아웃 완료");
        }

        return "web/login";
    }

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

    // ========================================
    // 회원 페이지 - user 폴더 (USER 권한 필요)
    // ========================================

    @GetMapping("/mypage")
    public String mypagePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        log.info("마이페이지 접속: {}, 권한: {}", auth.getName(), auth.getAuthorities());
        return "user/mypage";
    }

    // ========================================
    // 관리자 페이지 - manager 폴더 (ADMIN 권한 필요)
    // ========================================

    @GetMapping("/admin")
    public String adminPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        log.info("관리자 페이지 접속: {}", auth.getName());
        return "manager/admin";
    }

    // ========================================
    // 회사 정보 페이지 - company 폴더 (로그인 불필요)
    // ========================================

    @GetMapping("/about")
    public String about(Model model) {
        log.info("회사소개 페이지 접속");
        return "company/about";
    }

    @GetMapping("/products")
    public String products(Model model) {
        log.info("상품소개 페이지 접속");
        return "company/products";
    }

    // ========================================
    // 게시판 메인 - board 폴더 (로그인 불필요)
    // ========================================

    @GetMapping("/board")
    public String board(Model model) {
        log.info("게시판 페이지 접속");
        return "board/board";
    }
}
