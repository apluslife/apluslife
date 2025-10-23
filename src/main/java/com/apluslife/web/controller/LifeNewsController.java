package com.apluslife.web.controller;

import com.apluslife.domain.lifenews.dto.LifeNewsDto;
import com.apluslife.domain.lifenews.dto.LifeNewsRequest;
import com.apluslife.domain.lifenews.service.LifeNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 라이프뉴스 관리 컨트롤러
 * 관리자용 라이프뉴스 게시판 제어
 */
@Slf4j
@Controller
@RequestMapping("/admin/lifenews")
@RequiredArgsConstructor
public class LifeNewsController {

    private final LifeNewsService lifeNewsService;

    /**
     * 라이프뉴스 목록 페이지
     */
    @GetMapping("/list")
    public String list(Model model) {
        try {
            List<LifeNewsDto> lifeNewsList = lifeNewsService.getLifeNewsList();
            model.addAttribute("lifeNewsList", lifeNewsList);
            return "manager/lifenews/list";
        } catch (Exception e) {
            log.error("라이프뉴스 목록 조회 오류", e);
            model.addAttribute("errorMessage", "라이프뉴스 목록 조회에 실패했습니다.");
            return "manager/lifenews/list";
        }
    }

    /**
     * 라이프뉴스 상세 조회 페이지
     */
    @GetMapping("/detail/{idx}")
    public String detail(@PathVariable Integer idx, Model model) {
        try {
            LifeNewsDto lifeNews = lifeNewsService.getLifeNews(idx);
            model.addAttribute("lifeNews", lifeNews);
            return "manager/lifenews/detail";
        } catch (Exception e) {
            log.error("라이프뉴스 상세 조회 오류", e);
            model.addAttribute("errorMessage", "라이프뉴스 조회에 실패했습니다.");
            return "redirect:/admin/lifenews/list";
        }
    }

    /**
     * 라이프뉴스 작성 페이지
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("lifeNews", new LifeNewsRequest());
        return "manager/lifenews/create";
    }

    /**
     * 라이프뉴스 등록 처리
     */
    @PostMapping("/create")
    public String createPost(LifeNewsRequest request, RedirectAttributes redirectAttributes) {
        try {
            Integer idx = lifeNewsService.createLifeNews(request);
            redirectAttributes.addFlashAttribute("successMessage", "라이프뉴스가 등록되었습니다.");
            return "redirect:/admin/lifenews/list";
        } catch (Exception e) {
            log.error("라이프뉴스 등록 오류", e);
            redirectAttributes.addFlashAttribute("errorMessage", "라이프뉴스 등록에 실패했습니다.");
            return "redirect:/admin/lifenews/create";
        }
    }

    /**
     * 라이프뉴스 수정 페이지
     */
    @GetMapping("/edit/{idx}")
    public String edit(@PathVariable Integer idx, Model model) {
        try {
            LifeNewsDto lifeNews = lifeNewsService.getLifeNews(idx);
            model.addAttribute("lifeNews", lifeNews);
            return "manager/lifenews/edit";
        } catch (Exception e) {
            log.error("라이프뉴스 수정 페이지 조회 오류", e);
            model.addAttribute("errorMessage", "라이프뉴스 조회에 실패했습니다.");
            return "redirect:/admin/lifenews/list";
        }
    }

    /**
     * 라이프뉴스 수정 처리
     */
    @PostMapping("/edit/{idx}")
    public String editPost(@PathVariable Integer idx, LifeNewsRequest request, RedirectAttributes redirectAttributes) {
        try {
            lifeNewsService.updateLifeNews(idx, request);
            redirectAttributes.addFlashAttribute("successMessage", "라이프뉴스가 수정되었습니다.");
            return "redirect:/admin/lifenews/detail/" + idx;
        } catch (Exception e) {
            log.error("라이프뉴스 수정 오류", e);
            redirectAttributes.addFlashAttribute("errorMessage", "라이프뉴스 수정에 실패했습니다.");
            return "redirect:/admin/lifenews/edit/" + idx;
        }
    }

    /**
     * 라이프뉴스 삭제 처리
     */
    @PostMapping("/delete/{idx}")
    public String delete(@PathVariable Integer idx, RedirectAttributes redirectAttributes) {
        try {
            lifeNewsService.deleteLifeNews(idx);
            redirectAttributes.addFlashAttribute("successMessage", "라이프뉴스가 삭제되었습니다.");
            return "redirect:/admin/lifenews/list";
        } catch (Exception e) {
            log.error("라이프뉴스 삭제 오류", e);
            redirectAttributes.addFlashAttribute("errorMessage", "라이프뉴스 삭제에 실패했습니다.");
            return "redirect:/admin/lifenews/detail/" + idx;
        }
    }

    /**
     * 라이프뉴스 다중 삭제 처리
     */
    @PostMapping("/delete")
    public String deleteList(@RequestParam List<Integer> selectedIdx, RedirectAttributes redirectAttributes) {
        try {
            if (selectedIdx == null || selectedIdx.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "삭제할 항목을 선택해주세요.");
                return "redirect:/admin/lifenews/list";
            }
            lifeNewsService.deleteLifeNewsList(selectedIdx);
            redirectAttributes.addFlashAttribute("successMessage", "선택된 라이프뉴스가 삭제되었습니다.");
            return "redirect:/admin/lifenews/list";
        } catch (Exception e) {
            log.error("라이프뉴스 다중 삭제 오류", e);
            redirectAttributes.addFlashAttribute("errorMessage", "라이프뉴스 삭제에 실패했습니다.");
            return "redirect:/admin/lifenews/list";
        }
    }

    /**
     * 라이프뉴스 검색
     */
    @GetMapping("/search")
    public String search(@RequestParam String searchText, Model model) {
        try {
            List<LifeNewsDto> lifeNewsList = lifeNewsService.searchLifeNews(searchText);
            model.addAttribute("lifeNewsList", lifeNewsList);
            model.addAttribute("searchText", searchText);
            return "manager/lifenews/list";
        } catch (Exception e) {
            log.error("라이프뉴스 검색 오류", e);
            model.addAttribute("errorMessage", "라이프뉴스 검색에 실패했습니다.");
            return "manager/lifenews/list";
        }
    }
}
