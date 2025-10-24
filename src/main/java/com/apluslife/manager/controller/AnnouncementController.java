package com.apluslife.manager.controller;

import com.apluslife.domain.announcement.dto.AnnouncementDto;
import com.apluslife.domain.announcement.dto.AnnouncementRequest;
import com.apluslife.domain.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * 공시자료 관리 컨트롤러
 * 관리자용 공시자료 CRUD 작업
 */
@Slf4j
@Controller
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 공시자료 목록 페이지
     */
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(required = false) String keyword,
                      Model model) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<AnnouncementDto> announcements;
        if (keyword != null && !keyword.isEmpty()) {
            announcements = announcementService.searchAnnouncements(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            announcements = announcementService.getAnnouncementList(pageable);
        }

        model.addAttribute("announcements", announcements);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", announcements.getTotalPages());

        return "manager/announcements/announcements-list";
    }

    /**
     * 공시자료 등록 페이지 (GET)
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("announcementRequest", new AnnouncementRequest());
        return "manager/announcements/announcements-create";
    }

    /**
     * 공시자료 등록 처리 (POST)
     */
    @PostMapping("/create")
    public String create(@ModelAttribute AnnouncementRequest request,
                        @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
                        @RequestParam(value = "wordFile", required = false) MultipartFile wordFile,
                        @RequestParam(value = "hwpFile", required = false) MultipartFile hwpFile,
                        RedirectAttributes redirectAttributes) {
        try {
            log.info("공시자료 등록 요청 - 제목: {}", request.getTitle());

            // 제목 검증
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                log.warn("제목이 비어있습니다");
                redirectAttributes.addFlashAttribute("error", "공시자료 제목을 입력하세요.");
                return "redirect:/admin/announcements/create";
            }

            // 공시자료 생성 (파일 없어도 저장 가능)
            Integer announcementIdx = announcementService.createAnnouncement(
                    request, pdfFile, wordFile, hwpFile);

            log.info("공시자료 등록 성공 - idx: {}", announcementIdx);
            redirectAttributes.addFlashAttribute("message", "공시자료가 성공적으로 등록되었습니다.");
            return "redirect:/admin/announcements/detail/" + announcementIdx;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "파일 업로드에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/announcements/create";
        } catch (Exception e) {
            log.error("공시자료 등록 실패", e);
            redirectAttributes.addFlashAttribute("error", "공시자료 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/announcements/create";
        }
    }

    /**
     * 공시자료 상세 조회 페이지
     */
    @GetMapping("/detail/{idx}")
    public String detail(@PathVariable Integer idx, Model model) {
        try {
            AnnouncementDto announcement = announcementService.getAnnouncement(idx);
            model.addAttribute("announcement", announcement);
            return "manager/announcements/announcements-detail";
        } catch (Exception e) {
            log.error("공시자료 상세 조회 실패", e);
            return "redirect:/admin/announcements/list";
        }
    }

    /**
     * 공시자료 수정 페이지 (GET)
     */
    @GetMapping("/edit/{idx}")
    public String editForm(@PathVariable Integer idx, Model model) {
        try {
            AnnouncementDto announcement = announcementService.getAnnouncement(idx);
            model.addAttribute("announcement", announcement);
            model.addAttribute("announcementRequest", AnnouncementRequest.builder()
                    .title(announcement.getTitle())
                    .build());
            return "manager/announcements/announcements-edit";
        } catch (Exception e) {
            log.error("공시자료 수정 페이지 로드 실패", e);
            return "redirect:/admin/announcements/list";
        }
    }

    /**
     * 공시자료 수정 처리 (POST)
     */
    @PostMapping("/edit/{idx}")
    public String edit(@PathVariable Integer idx,
                      @ModelAttribute AnnouncementRequest request,
                      @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
                      @RequestParam(value = "wordFile", required = false) MultipartFile wordFile,
                      @RequestParam(value = "hwpFile", required = false) MultipartFile hwpFile,
                      @RequestParam(value = "replaceFiles", required = false) Boolean replaceFiles,
                      RedirectAttributes redirectAttributes) {
        try {
            // 제목 검증
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "공시자료 제목을 입력하세요.");
                return "redirect:/admin/announcements/edit/" + idx;
            }

            announcementService.updateAnnouncement(idx, request, pdfFile, wordFile, hwpFile, replaceFiles);

            redirectAttributes.addFlashAttribute("message", "공시자료가 성공적으로 수정되었습니다.");
            return "redirect:/admin/announcements/detail/" + idx;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "파일 업로드에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/announcements/edit/" + idx;
        } catch (Exception e) {
            log.error("공시자료 수정 실패", e);
            redirectAttributes.addFlashAttribute("error", "공시자료 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/announcements/edit/" + idx;
        }
    }

    /**
     * 공시자료 삭제 처리
     */
    @GetMapping("/delete/{idx}")
    public String delete(@PathVariable Integer idx,
                        RedirectAttributes redirectAttributes) {
        try {
            announcementService.deleteAnnouncement(idx);
            redirectAttributes.addFlashAttribute("message", "공시자료가 성공적으로 삭제되었습니다.");
            return "redirect:/admin/announcements/list";
        } catch (Exception e) {
            log.error("공시자료 삭제 실패", e);
            redirectAttributes.addFlashAttribute("error", "공시자료 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/announcements/list";
        }
    }

    /**
     * 공시자료 API 조회 (JSON)
     */
    @GetMapping("/api/{idx}")
    @ResponseBody
    public ResponseEntity<AnnouncementDto> getAnnouncementApi(@PathVariable Integer idx) {
        try {
            AnnouncementDto announcement = announcementService.getAnnouncement(idx);
            return ResponseEntity.ok(announcement);
        } catch (Exception e) {
            log.error("공시자료 API 조회 실패", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 공시자료 목록 API (JSON)
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Page<AnnouncementDto>> getAnnouncementsApi(
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<AnnouncementDto> announcements = announcementService.getAnnouncementList(pageable);
        return ResponseEntity.ok(announcements);
    }
}
