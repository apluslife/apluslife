package com.apluslife.web.controller;

import com.apluslife.domain.news.dto.NewsDto;
import com.apluslife.domain.news.dto.NewsRequest;
import com.apluslife.domain.news.service.NewsService;
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
 * 라이프뉴스 관리 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * 뉴스 목록 페이지
     */
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(required = false) String keyword,
                      @RequestParam(required = false) String category,
                      Model model) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<NewsDto> news;
        if (keyword != null && !keyword.isEmpty()) {
            news = newsService.searchNews(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.isEmpty()) {
            news = newsService.getNewsByCategory(category, pageable);
            model.addAttribute("category", category);
        } else {
            news = newsService.getNewsList(pageable);
        }

        model.addAttribute("news", news);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", news.getTotalPages());

        return "manager/news/news-list";
    }

    /**
     * 뉴스 등록 페이지 (GET)
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("newsRequest", new NewsRequest());
        return "manager/news/news-create";
    }

    /**
     * 뉴스 등록 처리 (POST)
     */
    @PostMapping("/create")
    public String create(@ModelAttribute NewsRequest request,
                        @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnailFile,
                        RedirectAttributes redirectAttributes) {
        try {
            // 검증
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "뉴스 제목을 입력하세요.");
                return "redirect:/admin/news/create";
            }

            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "뉴스 내용을 입력하세요.");
                return "redirect:/admin/news/create";
            }

            Integer newsIdx = newsService.createNews(request, thumbnailFile);

            redirectAttributes.addFlashAttribute("message", "뉴스가 성공적으로 등록되었습니다.");
            return "redirect:/admin/news/detail/" + newsIdx;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "파일 업로드에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/news/create";
        } catch (Exception e) {
            log.error("뉴스 등록 실패", e);
            redirectAttributes.addFlashAttribute("error", "뉴스 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/news/create";
        }
    }

    /**
     * 뉴스 상세 조회 페이지
     */
    @GetMapping("/detail/{idx}")
    public String detail(@PathVariable Integer idx, Model model) {
        try {
            NewsDto news = newsService.getNews(idx);
            model.addAttribute("news", news);
            return "manager/news/news-detail";
        } catch (Exception e) {
            log.error("뉴스 상세 조회 실패", e);
            return "redirect:/admin/news/list";
        }
    }

    /**
     * 뉴스 수정 페이지 (GET)
     */
    @GetMapping("/edit/{idx}")
    public String editForm(@PathVariable Integer idx, Model model) {
        try {
            NewsDto news = newsService.getNews(idx);
            model.addAttribute("news", news);
            model.addAttribute("newsRequest", NewsRequest.builder()
                    .title(news.getTitle())
                    .content(news.getContent())
                    .summary(news.getSummary())
                    .category(news.getCategory())
                    .isNotice(news.getIsNotice())
                    .build());
            return "manager/news/news-edit";
        } catch (Exception e) {
            log.error("뉴스 수정 페이지 로드 실패", e);
            return "redirect:/admin/news/list";
        }
    }

    /**
     * 뉴스 수정 처리 (POST)
     */
    @PostMapping("/edit/{idx}")
    public String edit(@PathVariable Integer idx,
                      @ModelAttribute NewsRequest request,
                      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnailFile,
                      RedirectAttributes redirectAttributes) {
        try {
            // 검증
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "뉴스 제목을 입력하세요.");
                return "redirect:/admin/news/edit/" + idx;
            }

            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "뉴스 내용을 입력하세요.");
                return "redirect:/admin/news/edit/" + idx;
            }

            newsService.updateNews(idx, request, thumbnailFile);

            redirectAttributes.addFlashAttribute("message", "뉴스가 성공적으로 수정되었습니다.");
            return "redirect:/admin/news/detail/" + idx;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            redirectAttributes.addFlashAttribute("error", "파일 업로드에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/news/edit/" + idx;
        } catch (Exception e) {
            log.error("뉴스 수정 실패", e);
            redirectAttributes.addFlashAttribute("error", "뉴스 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/news/edit/" + idx;
        }
    }

    /**
     * 뉴스 삭제 처리
     */
    @GetMapping("/delete/{idx}")
    public String delete(@PathVariable Integer idx,
                        RedirectAttributes redirectAttributes) {
        try {
            newsService.deleteNews(idx);
            redirectAttributes.addFlashAttribute("message", "뉴스가 성공적으로 삭제되었습니다.");
            return "redirect:/admin/news/list";
        } catch (Exception e) {
            log.error("뉴스 삭제 실패", e);
            redirectAttributes.addFlashAttribute("error", "뉴스 삭제에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/news/list";
        }
    }

    /**
     * 공지 여부 토글
     */
    @PostMapping("/toggle-notice/{idx}")
    @ResponseBody
    public ResponseEntity<?> toggleNotice(@PathVariable Integer idx) {
        try {
            newsService.toggleNotice(idx);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            log.error("공지 토글 실패", e);
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * 뉴스 API 조회 (JSON)
     */
    @GetMapping("/api/{idx}")
    @ResponseBody
    public ResponseEntity<NewsDto> getNewsApi(@PathVariable Integer idx) {
        try {
            NewsDto news = newsService.getNews(idx);
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            log.error("뉴스 API 조회 실패", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 뉴스 목록 API (JSON)
     */
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<Page<NewsDto>> getNewsListApi(
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<NewsDto> news = newsService.getNewsList(pageable);
        return ResponseEntity.ok(news);
    }

    /**
     * 인기 뉴스 API
     */
    @GetMapping("/api/popular")
    @ResponseBody
    public ResponseEntity<Page<NewsDto>> getPopularNewsApi(
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<NewsDto> news = newsService.getPopularNews(pageable);
        return ResponseEntity.ok(news);
    }
}
